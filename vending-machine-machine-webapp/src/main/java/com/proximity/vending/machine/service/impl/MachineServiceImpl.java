package com.proximity.vending.machine.service.impl;

import com.proximity.vending.domain.exception.*;
import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.type.TransactionType;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.machine.config.VendingMachineProperties;
import com.proximity.vending.machine.dto.CardDTO;
import com.proximity.vending.machine.dto.CashDTO;
import com.proximity.vending.machine.dto.ChangeDTO;
import com.proximity.vending.machine.dto.ReceiptDTO;
import com.proximity.vending.machine.service.MachineService;
import com.proximity.vending.machine.service.dto.CardPaymentDTO;
import com.proximity.vending.machine.service.dto.CashPaymentDTO;
import com.proximity.vending.machine.service.dto.TransactionDTO;
import com.proximity.vending.machine.service.dto.VendingMachineDTO;
import com.proximity.vending.machine.service.mapper.ChangeDTOMapper;
import com.proximity.vending.machine.service.mapper.ReceiptDTOMapper;
import com.proximity.vending.machine.service.mapper.TransactionMachineMapper;
import com.proximity.vending.machine.service.mapper.VendingMachineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private static final String GET_VENDING_MACHINE_PATH = "/vending-machine/{code}";
    private static final String OPEN_MACHINE_PATH = "/vending-machine/{code}/open";
    private static final String CARD_TRANSACTION_PATH = "/transactions/card";
    private static final String CASH_TRANSACTION_PATH = "/transactions/cash";

    private final WebClient adminWebClient;
    private final VendingMachineProperties machineProperties;

    private final VendingMachineMapper vendingMachineMapper;
    private final TransactionMachineMapper transactionMachineMapper;
    private final ChangeDTOMapper changeDTOMapper;
    private final ReceiptDTOMapper receiptDTOMapper;

    @Override
    public Mono<ReceiptDTO> cardTransaction(CardDTO cardDTO) {
        Mono<VendingMachine> vendingMachineMono = this.adminWebClient
                .get()
                .uri(GET_VENDING_MACHINE_PATH, this.machineProperties.getId())
                .retrieve()
                .bodyToMono(VendingMachineDTO.class)
                .map(this.vendingMachineMapper::map)
                .switchIfEmpty(Mono.error(new EmptyStackException()));

        CardPaymentDTO cashPaymentDTO = CardPaymentDTO.builder()
                .productID(cardDTO.getProduct())
                .vendingMachineID(this.machineProperties.getId())
                .issuer(cardDTO.getIssuer())
                .build();

        Mono<Transaction> transactionMono = this.adminWebClient
                .post()
                .uri(CARD_TRANSACTION_PATH)
                .body(BodyInserters.fromValue(cashPaymentDTO))
                .retrieve()
                .bodyToMono(TransactionDTO.class)
                .map(this.transactionMachineMapper::map)
                .switchIfEmpty(Mono.error(new EmptyStackException()));

        return vendingMachineMono.
                doOnSuccess(vendingMachine -> {
                    if (!vendingMachine.supportsTransactionType(TransactionType.CARD)) {
                        throw new UnsupportedOperationException("Machine does not support Transaction Type");
                    }
                    if (!vendingMachine.hasStock(ProductID.of(cardDTO.getProduct()))) {
                        throw new UnsupportedOperationException("Machine does not have stock for Product");
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .flatMap(vendingMachine -> transactionMono)
                .doOnError(Throwable::printStackTrace)
                .flatMap(transaction -> Mono.just(this.receiptDTOMapper.map(transaction)));
    }

    @Override
    public Mono<ChangeDTO> cashTransaction(CashDTO cashDTO) {
        Mono<VendingMachine> vendingMachineMono = this.adminWebClient
                .get()
                .uri(GET_VENDING_MACHINE_PATH, this.machineProperties.getId())
                .retrieve()
                .bodyToMono(VendingMachineDTO.class)
                .map(this.vendingMachineMapper::map)
                .switchIfEmpty(Mono.error(new EmptyStackException()));

        CashPaymentDTO cashPaymentDTO = CashPaymentDTO.builder()
                .productID(cashDTO.getProduct())
                .vendingMachineID(this.machineProperties.getId())
                .insertedDenomination(cashDTO.getCash())
                .build();

        Mono<Transaction> transactionMono = this.adminWebClient
                .post()
                .uri(CASH_TRANSACTION_PATH)
                .body(BodyInserters.fromValue(cashPaymentDTO))
                .retrieve()
                .bodyToMono(TransactionDTO.class)
                .map(this.transactionMachineMapper::map)
                .switchIfEmpty(Mono.error(new EmptyStackException()));

        Map<Denomination, Integer> change = new HashMap<>();

        return vendingMachineMono.
                doOnSuccess(vendingMachine -> {
                    Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED), () -> new BlockedMachineException(vendingMachine.getVendingMachineID()));
                    Preconditions.checkArgument(!vendingMachine.supportsTransactionType(TransactionType.CASH), () -> new InvalidDataException(TransactionType.CASH));

                    ProductID productID = ProductID.of(cashDTO.getProduct());
                    Preconditions.checkArgument(!vendingMachine.hasStock(productID), () -> OutOfStockException.builder()
                            .productID(productID)
                            .vendingMachineID(vendingMachine.getVendingMachineID())
                            .build());
                    /* if (!vendingMachine.supportsTransactionType(TransactionType.CASH)) {
                        throw new UnsupportedOperationException("Machine does not support Transaction Type");
                    }
                    if (!vendingMachine.hasStock(productID)) {
                        throw new UnsupportedOperationException("Machine does not have stock for Product");
                    } */

                    BigDecimal paidAmount = cashDTO.getPaidAmount();
                    BigDecimal productPrice = vendingMachine.getProductPrice(productID);
                    Preconditions.checkArgument(productPrice.compareTo(paidAmount) > 0, () -> new InvalidDataException(paidAmount));
                    /* if (productPrice.compareTo(paidAmount) > 0) {
                        throw new UnsupportedOperationException("Not enough cash to pay for Product");
                    } */

                    BigDecimal changeAmount = paidAmount.subtract(productPrice);
                    Preconditions.checkArgument(!vendingMachine.hasChange(changeAmount), () -> CashChangeException.builder()
                            .changeAmount(changeAmount)
                            .vendingMachineID(vendingMachine.getVendingMachineID())
                            .build());
                    /* if (!vendingMachine.hasChange(changeAmount)) {
                        throw new UnsupportedOperationException("Not enough change in Machine");
                    } */

                    change.putAll(vendingMachine.calculateChange(changeAmount));
                })
                .doOnError(Throwable::printStackTrace)
                .flatMap(vendingMachine -> transactionMono)
                .doOnError(Throwable::printStackTrace)
                .flatMap(transaction -> Mono.just(this.changeDTOMapper.map(transaction, change)));
    }
}

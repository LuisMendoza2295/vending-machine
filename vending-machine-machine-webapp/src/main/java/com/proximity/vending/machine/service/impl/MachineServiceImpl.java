package com.proximity.vending.machine.service.impl;

import com.proximity.vending.domain.exception.*;
import com.proximity.vending.domain.exception.commons.BaseException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private static final String GET_VENDING_MACHINE_PATH = "/vending-machine/{code}";
    private static final String OPEN_MACHINE_PATH = "/vending-machine/{code}/open";
    private static final String CLOSE_MACHINE_PATH = "/vending-machine/{code}/close";
    private static final String CARD_TRANSACTION_PATH = "/transactions/card";
    private static final String CASH_TRANSACTION_PATH = "/transactions/cash";

    private static final String ACCESS_CODE_PARAM = "accessCode";

    private final WebClient adminWebClient;
    private final VendingMachineProperties machineProperties;

    private final VendingMachineMapper vendingMachineMapper;
    private final TransactionMachineMapper transactionMachineMapper;
    private final ChangeDTOMapper changeDTOMapper;
    private final ReceiptDTOMapper receiptDTOMapper;

    @Override
    public Mono<ReceiptDTO> cardTransaction(CardDTO cardDTO) {
        Mono<VendingMachine> vendingMachineMono = this.getFindVendingMachineMono();

        CardPaymentDTO cardPaymentDTO = CardPaymentDTO.builder()
                .productID(cardDTO.getProduct())
                .vendingMachineID(this.machineProperties.getId())
                .issuer(cardDTO.getIssuer())
                .build();

        Mono<Transaction> transactionMono = this.adminWebClient
                .post()
                .uri(CARD_TRANSACTION_PATH)
                .body(BodyInserters.fromValue(cardPaymentDTO))
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> clientResponse.body(BodyExtractors.toMono(BaseException.class)).flatMap(Mono::error)
                )
                .bodyToMono(TransactionDTO.class)
                .map(this.transactionMachineMapper::map)
                .switchIfEmpty(Mono.error(new NotFoundEntityException(Transaction.class)));

        return vendingMachineMono.
                doOnSuccess(vendingMachine -> {
                    Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED), () -> new BlockedMachineException(vendingMachine.getVendingMachineID()));
                    Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.OPEN), () -> new OpenMachineException(vendingMachine.getVendingMachineID()));
                    Preconditions.checkNotArgument(vendingMachine.supportsTransactionType(TransactionType.CARD), () -> new InvalidDataException(TransactionType.CARD));
                })
                .doOnError(e -> log.error("COULD NOT PROCESS VENDING MACHINE PRECONDITIONS"))
                .flatMap(vendingMachine -> transactionMono)
                .doOnError(e -> log.error("COULD NOT PROCESS CARD TRANSACTION"))
                .flatMap(transaction -> Mono.just(this.receiptDTOMapper.map(transaction)));
    }

    @Override
    public Mono<ChangeDTO> cashTransaction(CashDTO cashDTO) {
        Mono<VendingMachine> vendingMachineMono = this.getFindVendingMachineMono();

        Map<Denomination, Integer> change = new HashMap<>();

        return vendingMachineMono.
                doOnSuccess(vendingMachine -> {
                    ProductID productID = ProductID.of(cashDTO.getProduct());
                    Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED), () -> new BlockedMachineException(vendingMachine.getVendingMachineID()));
                    Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.OPEN), () -> new OpenMachineException(vendingMachine.getVendingMachineID()));
                    Preconditions.checkNotArgument(vendingMachine.supportsTransactionType(TransactionType.CASH), () -> new InvalidDataException(TransactionType.CASH));
                    Preconditions.checkNotArgument(vendingMachine.hasStock(productID), () -> new OutOfStockException(vendingMachine.getVendingMachineID(), productID));

                    BigDecimal paidAmount = cashDTO.getPaidAmount();
                    BigDecimal productPrice = vendingMachine.getProductPrice(productID);
                    Preconditions.checkArgument(productPrice.compareTo(paidAmount) > 0, () -> new InvalidDataException(paidAmount));

                    BigDecimal changeAmount = paidAmount.subtract(productPrice);
                    Preconditions.checkNotArgument(vendingMachine.hasChange(changeAmount), () -> new CashChangeException(vendingMachine.getVendingMachineID(), changeAmount));

                    change.putAll(vendingMachine.calculateChange(changeAmount));
                })
                .doOnError(e -> log.error("COULD NOT PROCESS VENDING MACHINE PRECONDITIONS"))
                .flatMap(vendingMachine -> {
                    cashDTO.getCash()
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(e -> Denomination.fromCode(e.getKey()), Map.Entry::getValue))
                            .forEach(vendingMachine::addCurrencyCount);
                    change.forEach(vendingMachine::subtractCurrencyCount);
                    return Mono.just(vendingMachine);
                })
                .flatMap(vendingMachine -> Mono.just(CashPaymentDTO.builder()
                        .productID(cashDTO.getProduct())
                        .vendingMachineID(this.machineProperties.getId())
                        .insertedDenominationCount(cashDTO.getCash())
                        .finalDenominationCount(vendingMachine.getDenominationCount()
                                .entrySet()
                                .stream()
                                .collect(Collectors.toMap(e -> e.getKey().getCode(), Map.Entry::getValue)))
                        .build()))
                .flatMap(cashPaymentDTO -> this.adminWebClient
                        .post()
                        .uri(CASH_TRANSACTION_PATH)
                        .body(BodyInserters.fromValue(cashPaymentDTO))
                        .retrieve()
                        .onStatus(
                                httpStatus -> !httpStatus.is2xxSuccessful(),
                                clientResponse -> clientResponse.body(BodyExtractors.toMono(BaseException.class)).flatMap(Mono::error)
                        )
                        .bodyToMono(TransactionDTO.class)
                        .map(this.transactionMachineMapper::map)
                        .switchIfEmpty(Mono.error(new NotFoundEntityException(Transaction.class))))
                .doOnError(e -> log.error("COULD NOT PROCESS CASH TRANSACTION"))
                .flatMap(transaction -> Mono.just(this.changeDTOMapper.map(transaction, change)));
    }

    @Override
    public Mono<Boolean> openMachine(String code) {
        return this.adminWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(OPEN_MACHINE_PATH)
                        .queryParam(ACCESS_CODE_PARAM, code)
                        .build(this.machineProperties.getId()))
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> clientResponse.body(BodyExtractors.toMono(BaseException.class)).flatMap(Mono::error)
                )
                .bodyToMono(Boolean.class)
                .switchIfEmpty(Mono.error(new InvalidDataException(code)));
    }

    @Override
    public Mono<Boolean> closeMachine() {
        return this.adminWebClient
                .post()
                .uri(CLOSE_MACHINE_PATH, this.machineProperties.getId())
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> clientResponse.body(BodyExtractors.toMono(BaseException.class)).flatMap(Mono::error)
                )
                .bodyToMono(Boolean.class)
                .switchIfEmpty(Mono.error(new InvalidDataException(this.machineProperties.getId())));
    }

    private Mono<VendingMachine> getFindVendingMachineMono() {
        return this.adminWebClient
                .get()
                .uri(GET_VENDING_MACHINE_PATH, this.machineProperties.getId())
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> clientResponse.body(BodyExtractors.toMono(BaseException.class)).flatMap(Mono::error)
                )
                .bodyToMono(VendingMachineDTO.class)
                .map(this.vendingMachineMapper::map)
                .switchIfEmpty(Mono.error(new NotFoundEntityException(VendingMachine.class)));
    }
}

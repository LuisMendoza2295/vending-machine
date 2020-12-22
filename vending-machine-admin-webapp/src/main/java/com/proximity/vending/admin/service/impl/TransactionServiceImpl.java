package com.proximity.vending.admin.service.impl;

import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.admin.service.TransactionService;
import com.proximity.vending.admin.service.event.VendingMachineUpdatedEvent;
import com.proximity.vending.domain.exception.BlockedMachineException;
import com.proximity.vending.domain.exception.CardPaymentException;
import com.proximity.vending.domain.exception.Preconditions;
import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.repository.CardPaymentRepository;
import com.proximity.vending.domain.repository.TransactionRepository;
import com.proximity.vending.domain.repository.VendingMachineRepository;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.type.TransactionType;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.TransactionIssuer;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final VendingMachineRepository vendingMachineRepository;
    private final CardPaymentRepository cardPaymentRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Transaction createCashTransaction(String productID, String vendingMachineID, DenominationCountDTO finalDenominationCountDTO, DenominationCountDTO paidDenominationCountDTO) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CREATING CASH TRANSACTION FOR MACHINE {} ", vendingMachine);

        Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED), () -> new BlockedMachineException(vendingMachine.getVendingMachineID()));

        BigDecimal productPrice = vendingMachine.getProductPrice(ProductID.of(productID));
        log.info("CASH TRANSACTION PRODUCT {} WITH PRICE {}", productID, productPrice);

        Transaction transaction = Transaction.builder()
                .withGeneratedUUID()
                .productID(productID)
                .vendingMachineID(vendingMachineID)
                .type(TransactionType.CASH.getCode())
                .amount(productPrice)
                .dateTime(LocalDateTime.now())
                .build();
        finalDenominationCountDTO.getDenominationCount()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Denomination.fromCode(e.getKey()), Map.Entry::getValue))
                .forEach(vendingMachine::putCurrencyCount);
        paidDenominationCountDTO.getDenominationCount()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Denomination.fromCode(e.getKey()), Map.Entry::getValue))
                .forEach(transaction::addDenomination);
        log.info("CASH TRANSACTION TO BE CREATED {}", transaction);

        Transaction createdTransaction = this.transactionRepository.createTransaction(transaction);
        log.info("SAVED CASH TRANSACTION {}", createdTransaction);

        vendingMachine.dispenseProduct(ProductID.of(productID));

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.updateVendingMachine(vendingMachine);
        log.info("UPDATED VENDING MACHINE {}", updatedVendingMachine);

        eventPublisher.publishEvent(new VendingMachineUpdatedEvent(vendingMachine));

        return createdTransaction;
    }

    @Override
    @Transactional
    public Transaction createCardTransaction(String productID, String vendingMachineID, String issuer) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CREATING CARD TRANSACTION FOR MACHINE {} ", vendingMachine);

        Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED), () -> new BlockedMachineException(vendingMachine.getVendingMachineID()));

        BigDecimal productPrice = vendingMachine.getProductPrice(ProductID.of(productID));
        log.info("CARD TRANSACTION WITH CARD NUMBER {} FOR PRODUCT {} WITH PRICE {}", issuer, productID, productPrice);

        boolean cardPayment = this.cardPaymentRepository.pay(TransactionIssuer.of(issuer), productPrice);
        Preconditions.checkNotArgument(cardPayment, () -> new CardPaymentException(issuer));

        log.error("CARD TRANSACTION FOR {} ACCEPTED", issuer);

        Transaction transaction = Transaction.builder()
                .withGeneratedUUID()
                .productID(productID)
                .vendingMachineID(vendingMachineID)
                .type(TransactionType.CARD.getCode())
                .issuer(issuer)
                .amount(productPrice)
                .dateTime(LocalDateTime.now())
                .build();
        log.info("CARD TRANSACTION TO BE CREATED {}", transaction);

        Transaction createdTransaction = this.transactionRepository.createTransaction(transaction);
        log.info("SAVED CARD TRANSACTION {}", createdTransaction);

        vendingMachine.dispenseProduct(ProductID.of(productID));

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.updateVendingMachine(vendingMachine);
        log.info("UPDATED VENDING MACHINE {}", updatedVendingMachine);

        return createdTransaction;
    }

    @Override
    public List<Transaction> findAllBetweenDates(LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize) {
        log.info("SEARCHING TRANSACTIONS BETWEEN {} AND {} (PAGE {}, SIZE {})", from, to, pageNumber, pageSize);
        List<Transaction> transactions = this.transactionRepository.findAll(from, to, pageNumber, pageSize);
        log.info("TRANSACTIONS: {}", transactions);

        return transactions;
    }

    @Override
    public BigDecimal getTotalEarnings(LocalDateTime from, LocalDateTime to) {
        log.info("CALCULATING TOTAL EARNINGS BETWEEN {} AND {}", from, to);
        BigDecimal totalEarnings = this.transactionRepository.getTotalEarnings(from, to);
        log.info("TOTAL EARNINGS: {}", totalEarnings);

        return totalEarnings;
    }
}

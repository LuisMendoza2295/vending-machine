package com.proximity.vending.persistence.impl;

import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.repository.TransactionRepository;
import com.proximity.vending.domain.vo.VendingMachineID;
import com.proximity.vending.persistence.entities.ProductEntity;
import com.proximity.vending.persistence.entities.TransactionEntity;
import com.proximity.vending.persistence.entities.VendingMachineEntity;
import com.proximity.vending.persistence.mapper.TransactionDBMapper;
import com.proximity.vending.persistence.repository.ProductJpaRepository;
import com.proximity.vending.persistence.repository.TransactionJpaRepository;
import com.proximity.vending.persistence.repository.VendingMachineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EmptyStackException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryDB implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final VendingMachineJpaRepository vendingMachineJpaRepository;

    private final TransactionDBMapper transactionDBMapper;

    @Override
    public List<Transaction> findAllByVendingMachineID(VendingMachineID vendingMachineID, LocalDate localDate) {
        LocalDateTime from = localDate.atStartOfDay();
        LocalDateTime to = localDate.plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .minus(1, ChronoUnit.MILLIS);

        List<TransactionEntity> transactionEntities = this.transactionJpaRepository.findAllByVendingMachineBetweenDates(vendingMachineID.getValue(), from, to);

        return transactionEntities.stream()
                .map(this.transactionDBMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        ProductEntity productEntity = this.productJpaRepository.findByCode(transaction.getProductID().getValue())
                .orElseThrow(EmptyStackException::new);

        VendingMachineEntity vendingMachineEntity = this.vendingMachineJpaRepository.findByCode(transaction.getVendingMachineID().getValue())
                .orElseThrow(EmptyStackException::new);

        TransactionEntity transactionEntity = this.transactionDBMapper.map(transaction, productEntity, vendingMachineEntity);

        TransactionEntity savedTransactionEntity = this.transactionJpaRepository.save(transactionEntity);

        return this.transactionDBMapper.map(savedTransactionEntity);
    }
}

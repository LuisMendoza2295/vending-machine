package com.proximity.vending.persistence.mapper;

import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.persistence.entities.*;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class TransactionDBMapper {

    public Transaction map(TransactionEntity transactionEntity) {
        Transaction transaction = Transaction.builder()
                .uuid(transactionEntity.getUuid())
                .productID(transactionEntity.getProductEntity().getCode())
                .vendingMachineID(transactionEntity.getVendingMachineEntity().getCode())
                .type(transactionEntity.getType())
                .issuer(transactionEntity.getIssuer())
                .amount(transactionEntity.getAmount())
                .dateTime(transactionEntity.getTime())
                .build();

        transactionEntity.getTransactionDetailEntities()
                .forEach(detailEntity -> {
                    Denomination denomination = Denomination.fromCode(detailEntity.getCurrencyDenomination());
                    transaction.addDenomination(denomination, detailEntity.getCount());
                });

        return transaction;
    }

    public TransactionEntity map(Transaction transaction, ProductEntity productEntity, VendingMachineEntity vendingMachineEntity) {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .uuid(transaction.getTransactionID().getValue())
                .type(transaction.getType().getCode())
                .amount(transaction.getAmount().getValue())
                .issuer(transaction.getIssuer().getValue())
                .time(transaction.getDateTime())
                .productEntity(productEntity)
                .vendingMachineEntity(vendingMachineEntity)
                .transactionDetailEntities(new HashSet<>())
                .build();

        transaction.getInsertedDenomination()
                .entrySet()
                .stream()
                .map(denominationEntry -> mapDetailEntity(denominationEntry.getKey(), denominationEntry.getValue()))
                .collect(Collectors.toSet())
                .forEach(transactionEntity::addTransactionDetailEntity);

        return transactionEntity;
    }

    private TransactionDetailEntity mapDetailEntity(Denomination denomination, Integer count) {
        return TransactionDetailEntity.builder()
                .currencyDenomination(denomination.getCode())
                .currencyType(denomination.getCurrencyType().getCode())
                .count(count)
                .build();
    }
}

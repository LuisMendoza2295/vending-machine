package com.proximity.vending.admin.mapper;

import com.proximity.vending.admin.dto.TransactionDTO;
import com.proximity.vending.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransactionDTOMapper {

    public TransactionDTO map(Transaction transaction) {
        return TransactionDTO.builder()
                .transactionID(transaction.getTransactionID().getValue())
                .productID(transaction.getProductID().getValue())
                .vendingMachineID(transaction.getVendingMachineID().getValue())
                .amount(transaction.getAmount().getValue())
                .type(transaction.getType().getCode())
                .issuer(transaction.getIssuer().getValue())
                .dateTime(transaction.getDateTime())
                .insertedDenomination(transaction.getInsertedDenomination()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().getCode(), Map.Entry::getValue)))
                .build();
    }
}

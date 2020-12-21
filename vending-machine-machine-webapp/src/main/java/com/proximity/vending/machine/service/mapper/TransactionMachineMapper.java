package com.proximity.vending.machine.service.mapper;

import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.machine.service.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransactionMachineMapper {

    public Transaction map(TransactionDTO transactionDTO) {
        Transaction transaction = Transaction.builder()
                .uuid(transactionDTO.getTransactionID())
                .productID(transactionDTO.getProductID())
                .vendingMachineID(transactionDTO.getVendingMachineID())
                .type(transactionDTO.getType())
                .amount(transactionDTO.getAmount())
                .dateTime(transactionDTO.getDateTime())
                .issuer(transactionDTO.getIssuer())
                .build();

        transactionDTO.getInsertedDenomination()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Denomination.fromCode(e.getKey()), Map.Entry::getValue))
                .forEach(transaction::addDenomination);

        return transaction;
    }
}

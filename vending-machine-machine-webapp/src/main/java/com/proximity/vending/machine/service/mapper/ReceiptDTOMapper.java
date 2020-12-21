package com.proximity.vending.machine.service.mapper;

import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.machine.dto.ReceiptDTO;
import org.springframework.stereotype.Component;

@Component
public class ReceiptDTOMapper {

    public ReceiptDTO map(Transaction transaction) {
        return ReceiptDTO.builder()
                .issuer(transaction.getIssuer().getValue())
                .amount(transaction.getAmount().getValue())
                .dateTime(transaction.getDateTime())
                .product(transaction.getProductID().getValue())
                .vendingMachine(transaction.getVendingMachineID().getValue())
                .build();
    }
}

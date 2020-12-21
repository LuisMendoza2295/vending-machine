package com.proximity.vending.admin.service;

import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.domain.model.Transaction;

public interface TransactionService {

    Transaction createCashTransaction(String productID, String vendingMachineID, DenominationCountDTO denominationCountDTO);

    Transaction createCardTransaction(String productID, String vendingMachineID, String issuer);
}

package com.proximity.vending.domain.repository;

import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.vo.VendingMachineID;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {

    List<Transaction> findAllByVendingMachineID(VendingMachineID vendingMachineID, LocalDate localDate);

    Transaction createTransaction(Transaction transaction);
}

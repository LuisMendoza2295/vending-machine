package com.proximity.vending.domain.repository;

import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.vo.VendingMachineID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {

    List<Transaction> findAllByVendingMachineID(VendingMachineID vendingMachineID, LocalDate localDate);

    List<Transaction> findAll(LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);

    BigDecimal getTotalEarnings(LocalDateTime from, LocalDateTime to);

    Transaction createTransaction(Transaction transaction);
}

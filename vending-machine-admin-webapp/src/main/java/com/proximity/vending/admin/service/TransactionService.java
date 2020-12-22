package com.proximity.vending.admin.service;

import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    Transaction createCashTransaction(String productID, String vendingMachineID, DenominationCountDTO finalDenominationCountDTO, DenominationCountDTO paidDenominationCountDTO);

    Transaction createCardTransaction(String productID, String vendingMachineID, String issuer);

    List<Transaction> findAllBetweenDates(LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);

    BigDecimal getTotalEarnings(LocalDateTime from, LocalDateTime to);
}

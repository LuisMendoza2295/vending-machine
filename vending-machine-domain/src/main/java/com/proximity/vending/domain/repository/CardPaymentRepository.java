package com.proximity.vending.domain.repository;

import com.proximity.vending.domain.vo.TransactionIssuer;

import java.math.BigDecimal;

public interface CardPaymentRepository {

    boolean pay(TransactionIssuer issuer, BigDecimal amount);
}

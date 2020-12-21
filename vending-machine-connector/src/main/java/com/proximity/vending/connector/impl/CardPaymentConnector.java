package com.proximity.vending.connector.impl;

import com.proximity.vending.domain.repository.CardPaymentRepository;
import com.proximity.vending.domain.vo.TransactionIssuer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CardPaymentConnector implements CardPaymentRepository {

    @Override
    public boolean pay(TransactionIssuer issuer, BigDecimal amount) {
        return !issuer.getValue().startsWith("0");
    }
}

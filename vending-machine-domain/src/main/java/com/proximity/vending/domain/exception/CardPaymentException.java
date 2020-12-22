package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.exception.commons.ExceptionCode;
import lombok.Getter;

@Getter
public class CardPaymentException extends BaseException {

    private final String issuer;

    public CardPaymentException(String issuer) {
        super(ExceptionCode.CARD_PAYMENT.getCode());
        this.issuer = issuer;
    }
}

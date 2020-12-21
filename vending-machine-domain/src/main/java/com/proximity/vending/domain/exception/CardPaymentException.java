package com.proximity.vending.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardPaymentException extends RuntimeException {

    private final String issuer;
}

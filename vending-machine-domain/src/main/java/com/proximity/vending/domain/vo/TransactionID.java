package com.proximity.vending.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionID {

    @EqualsAndHashCode.Include
    String value;

    public static TransactionID of(String value) {
        return new TransactionID(value);
    }
}

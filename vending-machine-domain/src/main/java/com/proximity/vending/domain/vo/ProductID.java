package com.proximity.vending.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductID {

    @EqualsAndHashCode.Include
    String value;

    public static ProductID of(String value) {
        return new ProductID(value);
    }
}

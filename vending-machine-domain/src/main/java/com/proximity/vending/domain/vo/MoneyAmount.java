package com.proximity.vending.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyAmount {

    @EqualsAndHashCode.Include
    BigDecimal value;

    public static MoneyAmount of(BigDecimal value) {
        return new MoneyAmount(value);
    }
}

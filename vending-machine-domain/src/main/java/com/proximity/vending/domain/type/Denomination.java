package com.proximity.vending.domain.type;

import com.proximity.vending.domain.exception.NotFoundEntityException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Denomination {

    FIVE_CENTS(CurrencyType.COIN, BigDecimal.valueOf(0.05), "FIVE_CENT"),
    TEN_CENTS(CurrencyType.COIN, BigDecimal.valueOf(0.1), "TEN_CENT"),
    TWENTY_FIVE_CENTS(CurrencyType.COIN, BigDecimal.valueOf(0.25), "TWENTY_FIVE_CENT"),
    FIFTY_CENTS(CurrencyType.COIN, BigDecimal.valueOf(0.5), "FIFTY_CENT"),
    ONE_DOLLAR(CurrencyType.BILL, BigDecimal.valueOf(1), "ONE_DOLLAR"),
    TWO_DOLLARS(CurrencyType.BILL, BigDecimal.valueOf(2), "TWO_DOLLAR");

    private static final List<Denomination> COIN_TYPES = new ArrayList<>();
    private static final List<Denomination> BILL_TYPES = new ArrayList<>();
    private static final Map<String, Denomination> BY_CODE = new HashMap<>();

    static {
        COIN_TYPES.addAll(Arrays.stream(values())
                .filter(type -> type.currencyType.equals(CurrencyType.COIN))
                .collect(Collectors.toList()));

        BILL_TYPES.addAll(Arrays.stream(values())
                .filter(type -> type.currencyType.equals(CurrencyType.BILL))
                .collect(Collectors.toList()));

        BY_CODE.putAll(Arrays.stream(values())
                .collect(Collectors.toMap(Denomination::getCode, type -> type)));
    }

    public static List<Denomination> getCoinTypes() {
        return COIN_TYPES;
    }

    public static List<Denomination> getBillTypes() {
        return BILL_TYPES;
    }

    public static Denomination fromCode(String code) {
        return Optional.ofNullable(BY_CODE.get(code))
                .orElseThrow(() -> new NotFoundEntityException(Denomination.class));
    }

    private final CurrencyType currencyType;
    private final BigDecimal value;
    private final String code;

    public BigDecimal getCoinCentiValue() {
        return getCentiValue(this.value);
    }

    public static BigDecimal getCentiValue(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(100));
    }
}

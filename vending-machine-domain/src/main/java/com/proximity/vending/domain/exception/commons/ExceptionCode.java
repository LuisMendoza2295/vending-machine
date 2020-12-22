package com.proximity.vending.domain.exception.commons;

import com.proximity.vending.domain.exception.NotFoundEntityException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionCode {

    BLOCKED_MACHINE(ExceptionCodeConstants.BLOCKED_MACHINE_EXCEPTION),
    CARD_PAYMENT(ExceptionCodeConstants.CARD_PAYMENT_EXCEPTION),
    CASH_CHANGE(ExceptionCodeConstants.CASH_CHANGE_EXCEPTION),
    INVALID_DATA(ExceptionCodeConstants.INVALID_DATA_EXCEPTION),
    NOT_FOUND_ENTITY(ExceptionCodeConstants.NOT_FOUND_ENTITY_EXCEPTION),
    OPEN_MACHINE(ExceptionCodeConstants.OPEN_MACHINE_EXCEPTION),
    OUT_OF_STOCK(ExceptionCodeConstants.OUT_OF_STOCK_EXCEPTION);

    private static final Map<String, ExceptionCode> BY_CODE = new HashMap<>();

    static {
        BY_CODE.putAll(Arrays.stream(values())
                .collect(Collectors.toMap(ExceptionCode::getCode, type -> type)));
    }

    private final String code;

    public static ExceptionCode fromCode(String code) {
        return Optional.ofNullable(BY_CODE.get(code))
                .orElseThrow(() -> new NotFoundEntityException(ExceptionCode.class));
    }
}

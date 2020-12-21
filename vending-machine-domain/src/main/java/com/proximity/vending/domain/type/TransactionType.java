package com.proximity.vending.domain.type;

import com.proximity.vending.domain.exception.NotFoundEntityException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TransactionType {

    CASH("CASH"),
    CARD("CARD");

    private final String code;

    public static TransactionType fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException(TransactionType.class));
    }
}

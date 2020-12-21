package com.proximity.vending.domain.type;

import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.NotFoundEntityException;
import com.proximity.vending.domain.exception.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum VendingMachineStatus {
    OK("OK"),
    BLOCKED("BLOCKED"),
    FOR_MONEY_PICKUP("PICKUP");

    private static final int MAX_ATTEMPTS = 2;

    private final String code;

    public static VendingMachineStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException(VendingMachineStatus.class));
    }

    public static VendingMachineStatus fromAttempts(int attempts) {
        Preconditions.checkArgument(attempts < 0, () -> new InvalidDataException(attempts));
        if (attempts > MAX_ATTEMPTS) {
            return BLOCKED;
        }
        return OK;
    }
}

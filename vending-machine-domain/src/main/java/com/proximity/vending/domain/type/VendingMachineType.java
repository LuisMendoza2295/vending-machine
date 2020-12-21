package com.proximity.vending.domain.type;

import com.proximity.vending.domain.exception.NotFoundEntityException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum VendingMachineType {

    XYZ1("XYZ1", Stream.of(TransactionType.CASH).collect(Collectors.toCollection(HashSet::new))),
    XYZ2("XYZ2", Stream.of(TransactionType.CASH, TransactionType.CARD).collect(Collectors.toCollection(HashSet::new)));

    private final String code;
    private final Set<TransactionType> transactionTypes;

    public static VendingMachineType fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException(VendingMachineType.class));
    }
}

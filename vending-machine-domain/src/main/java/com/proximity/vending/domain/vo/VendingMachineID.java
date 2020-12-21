package com.proximity.vending.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VendingMachineID {

    @EqualsAndHashCode.Include
    String value;

    public static VendingMachineID of(String value) {
        return new VendingMachineID(value);
    }
}

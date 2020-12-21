package com.proximity.vending.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VendingMachineAccessCode {

    @EqualsAndHashCode.Include
    String value;

    public static VendingMachineAccessCode of(String value) {
        return new VendingMachineAccessCode(value);
    }
}

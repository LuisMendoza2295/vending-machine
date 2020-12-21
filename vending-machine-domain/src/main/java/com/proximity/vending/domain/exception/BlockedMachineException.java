package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockedMachineException extends RuntimeException {

    private final VendingMachineID vendingMachineID;
}

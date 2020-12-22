package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.exception.commons.ExceptionCode;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.Getter;

@Getter
public class BlockedMachineException extends BaseException {

    private final VendingMachineID vendingMachineID;

    public BlockedMachineException(VendingMachineID vendingMachineID) {
        super(ExceptionCode.BLOCKED_MACHINE.getCode());
        this.vendingMachineID = vendingMachineID;
    }
}

package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.exception.commons.ExceptionCode;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.Getter;

@Getter
public class OpenMachineException extends BaseException {

    private final VendingMachineID vendingMachineID;

    public OpenMachineException(VendingMachineID vendingMachineID) {
        super(ExceptionCode.OPEN_MACHINE.getCode());
        this.vendingMachineID = vendingMachineID;
    }
}

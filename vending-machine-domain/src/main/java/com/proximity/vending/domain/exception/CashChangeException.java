package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.exception.commons.ExceptionCode;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.*;

import java.math.BigDecimal;

@Getter
public class CashChangeException extends BaseException {

    private final VendingMachineID vendingMachineID;
    private final BigDecimal changeAmount;

    public CashChangeException(VendingMachineID vendingMachineID, BigDecimal changeAmount) {
        super(ExceptionCode.CASH_CHANGE.getCode());
        this.vendingMachineID = vendingMachineID;
        this.changeAmount = changeAmount;
    }
}

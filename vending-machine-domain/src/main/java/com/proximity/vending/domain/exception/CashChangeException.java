package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CashChangeException extends RuntimeException {

    private VendingMachineID vendingMachineID;
    private BigDecimal changeAmount;
}

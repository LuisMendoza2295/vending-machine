package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class OutOfStockException extends RuntimeException {

    private VendingMachineID vendingMachineID;
    private ProductID productID;
}

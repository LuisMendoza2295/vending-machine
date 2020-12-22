package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.exception.commons.ExceptionCodeConstants;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.*;

@Getter
public class OutOfStockException extends BaseException {

    private VendingMachineID vendingMachineID;
    private ProductID productID;

    public OutOfStockException(VendingMachineID vendingMachineID, ProductID productID) {
        super(ExceptionCodeConstants.OUT_OF_STOCK_EXCEPTION);
        this.vendingMachineID = vendingMachineID;
        this.productID = productID;
    }
}

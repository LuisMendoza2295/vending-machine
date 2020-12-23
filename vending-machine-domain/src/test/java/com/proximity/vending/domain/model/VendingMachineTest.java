package com.proximity.vending.domain.model;

import com.proximity.vending.domain.exception.CashChangeException;
import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.NotFoundEntityException;
import com.proximity.vending.domain.exception.OutOfStockException;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.type.VendingMachineType;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.VendingMachineAccessCode;
import com.proximity.vending.domain.vo.VendingMachineID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VendingMachineTest {

    private static final String CODE = "M001";
    private static final String ACCESS_CODE = "0000";
    private static final VendingMachineStatus STATUS = VendingMachineStatus.OK;
    private static final VendingMachineType TYPE = VendingMachineType.XYZ1;

    private VendingMachine.VendingMachineBuilder vendingMachineBuilder;

    @BeforeEach
    public void initEach() {
        vendingMachineBuilder = VendingMachine.builder()
                .code(CODE)
                .accessCode(ACCESS_CODE)
                .status(STATUS.getCode())
                .type(TYPE.getCode())
                .lastMoneyPickUp(LocalDateTime.now())
                .lastPing(LocalDateTime.now());
    }

    @Test
    public void testSuccessCreate() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();

        assertNotNull(vendingMachine);
        assertEquals(VendingMachineID.of(CODE), vendingMachine.getVendingMachineID());
        assertEquals(VendingMachineAccessCode.of(ACCESS_CODE), vendingMachine.getAccessCode());
        assertEquals(STATUS, vendingMachine.getStatus());
        assertEquals(TYPE, vendingMachine.getType());
    }

    @Test
    public void testCodeNull() {
        vendingMachineBuilder.code(null);
        assertThrows(InvalidDataException.class, () -> vendingMachineBuilder.build());
    }

    @Test
    public void testCodeEmpty() {
        vendingMachineBuilder.code("");
        assertThrows(InvalidDataException.class, vendingMachineBuilder::build);
    }

    @Test
    public void testInvalidStatus() {
        vendingMachineBuilder.status("ABC");
        assertThrows(NotFoundEntityException.class, vendingMachineBuilder::build);
    }

    @Test
    public void testInvalidType() {
        vendingMachineBuilder.type("ABC");
        assertThrows(NotFoundEntityException.class, vendingMachineBuilder::build);
    }

    @Test
    public void testPutProduct() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();

        ProductID productID = ProductID.of("P001");
        vendingMachine.putProduct(productID);

        assertTrue(vendingMachine.getProducts().containsKey(productID));
    }

    @Test
    public void testTotalVault() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();
        vendingMachine.putCurrencyCount(Denomination.FIFTY_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.TWENTY_FIVE_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.TEN_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.FIVE_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.ONE_DOLLAR, 1);
        vendingMachine.putCurrencyCount(Denomination.TWO_DOLLARS, 1);

        BigDecimal expected = BigDecimal.valueOf(3.90);
        BigDecimal actual = vendingMachine.getTotalVault();
        assertTrue(expected.compareTo(actual) == 0);
    }

    @Test
    public void testCashChange() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();
        vendingMachine.putCurrencyCount(Denomination.FIFTY_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.TWENTY_FIVE_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.TEN_CENTS, 0);
        vendingMachine.putCurrencyCount(Denomination.FIVE_CENTS, 1);

        BigDecimal amount = BigDecimal.valueOf(0.8);
        assertTrue(vendingMachine.hasChange(amount));

        Map<Denomination, Integer> change = vendingMachine.calculateChange(amount);

        assertNotNull(change);
        assertTrue(change.containsKey(Denomination.FIFTY_CENTS));
        assertEquals(Integer.valueOf(1), change.get(Denomination.FIFTY_CENTS));
        assertEquals(Integer.valueOf(1), change.get(Denomination.TWENTY_FIVE_CENTS));
        assertEquals(Integer.valueOf(1), change.get(Denomination.FIVE_CENTS));
    }

    @Test
    public void testNotEnoughChange() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();
        vendingMachine.putCurrencyCount(Denomination.FIFTY_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.TWENTY_FIVE_CENTS, 1);
        vendingMachine.putCurrencyCount(Denomination.TEN_CENTS, 0);
        vendingMachine.putCurrencyCount(Denomination.FIVE_CENTS, 1);

        BigDecimal amount = BigDecimal.valueOf(1);
        assertFalse(vendingMachine.hasChange(amount));

        assertThrows(CashChangeException.class, () -> vendingMachine.calculateChange(amount));
    }

    @Test
    public void testDispenseProduct() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();

        ProductID productID = ProductID.of("P001");
        vendingMachine.putProduct(productID, 1);
        assertTrue(vendingMachine.getProducts().containsKey(productID));
        assertEquals(Integer.valueOf(1), vendingMachine.getProducts().get(productID));

        VendingMachine updatedVendingMachine = vendingMachine.dispenseProduct(productID);
        assertTrue(updatedVendingMachine.getProducts().containsKey(productID));
        assertEquals(Integer.valueOf(0), updatedVendingMachine.getProducts().get(productID));
    }

    @Test
    public void testDispenseProductOutOfStock() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();

        ProductID productID = ProductID.of("P001");
        vendingMachine.putProduct(productID);
        assertTrue(vendingMachine.getProducts().containsKey(productID));

        assertThrows(OutOfStockException.class, () -> vendingMachine.dispenseProduct(productID));
    }

    @Test
    public void testDispenseNotExistingProduct() {
        VendingMachine vendingMachine = vendingMachineBuilder.build();

        ProductID productID = ProductID.of("P001");

        assertThrows(NotFoundEntityException.class, () -> vendingMachine.dispenseProduct(productID));
    }
}

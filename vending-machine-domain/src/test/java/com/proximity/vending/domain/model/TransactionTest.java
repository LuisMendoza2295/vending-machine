package com.proximity.vending.domain.model;

import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.NotFoundEntityException;
import com.proximity.vending.domain.type.TransactionType;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.TransactionID;
import com.proximity.vending.domain.vo.TransactionIssuer;
import com.proximity.vending.domain.vo.VendingMachineID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionTest {

    private static final String UUID = "4dbe3808-dc9c-44e2-966a-0a3d16131d57";
    private static final String PRODUCT_CODE = "P001";
    private static final String VENDING_MACHINE_ID = "M001";
    private static final TransactionType TYPE = TransactionType.CARD;
    private static final String ISSUER = "12345678";
    private static final BigDecimal AMOUNT = BigDecimal.ONE;

    private Transaction.TransactionBuilder transactionBuilder;

    @BeforeEach
    public void initEach() {
        transactionBuilder = Transaction.builder()
                .uuid(UUID)
                .productID(PRODUCT_CODE)
                .vendingMachineID(VENDING_MACHINE_ID)
                .type(TYPE.getCode())
                .issuer(ISSUER)
                .amount(AMOUNT)
                .dateTime(LocalDateTime.now());
    }

    @Test
    public void testSuccessCreate() {
        Transaction transaction = transactionBuilder.build();

        assertNotNull(transaction);
        assertEquals(TransactionID.of(UUID), transaction.getTransactionID());
        assertEquals(ProductID.of(PRODUCT_CODE), transaction.getProductID());
        assertEquals(VendingMachineID.of(VENDING_MACHINE_ID), transaction.getVendingMachineID());
        assertEquals(TYPE, transaction.getType());
        assertEquals(TransactionIssuer.of(ISSUER), transaction.getIssuer());
        assertTrue(AMOUNT.compareTo(transaction.getAmount().getValue()) == 0);
    }

    @Test
    public void testNullUUID() {
        transactionBuilder.uuid(null);
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testEmptyUUID() {
        transactionBuilder.uuid("");
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testNullProductID() {
        transactionBuilder.productID(null);
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testEmptyProductID() {
        transactionBuilder.productID("");
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testNullVendingMachineID() {
        transactionBuilder.vendingMachineID(null);
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testEmptyVendingMachineID() {
        transactionBuilder.vendingMachineID("");
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testNullType() {
        transactionBuilder.type(null);
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testInvalidType() {
        transactionBuilder.type("ABC");
        assertThrows(NotFoundEntityException.class, transactionBuilder::build);
    }

    @Test
    public void testNullAmount() {
        transactionBuilder.amount(null);
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testNegativeAmount() {
        transactionBuilder.amount(BigDecimal.valueOf(-1));
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }

    @Test
    public void testNullTime() {
        transactionBuilder.dateTime(null);
        assertThrows(InvalidDataException.class, transactionBuilder::build);
    }
}

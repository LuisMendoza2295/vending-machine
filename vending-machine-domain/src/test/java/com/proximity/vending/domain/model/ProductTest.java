package com.proximity.vending.domain.model;

import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.ProductName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductTest {

    private static final String CODE = "P001";
    private static final String NAME = "M&M";
    private static final BigDecimal PRICE = BigDecimal.ONE;

    private Product.ProductBuilder productBuilder;

    @BeforeEach
    public void initEach() {
        productBuilder = Product.builder()
                .code(CODE)
                .name(NAME)
                .price(PRICE);
    }

    @Test
    public void testSuccessCreate() {
        Product product = productBuilder.build();

        assertNotNull(product);
        assertEquals(ProductID.of(CODE), product.getProductID());
        assertEquals(ProductName.of(NAME), product.getName());
        assertTrue(PRICE.compareTo(product.getPrice().getValue()) == 0);
    }

    @Test
    public void testCodeNull() {
        productBuilder.code(null);
        assertThrows(InvalidDataException.class, productBuilder::build);
    }

    @Test
    public void testCodeEmpty() {
        productBuilder.code("");
        assertThrows(InvalidDataException.class, productBuilder::build);
    }

    @Test
    public void testNameNull() {
        productBuilder.name(null);
        assertThrows(InvalidDataException.class, productBuilder::build);
    }

    @Test
    public void testNameEmpty() {
        productBuilder.name("");
        assertThrows(InvalidDataException.class, productBuilder::build);
    }

    @Test
    public void testPriceNull() {
        productBuilder.price(null);
        assertThrows(InvalidDataException.class, productBuilder::build);
    }

    @Test
    public void testNegativePrice() {
        productBuilder.price(BigDecimal.valueOf(-1));
        assertThrows(InvalidDataException.class, productBuilder::build);
    }
}

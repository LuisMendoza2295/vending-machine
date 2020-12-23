package com.proximity.vending.persistence.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proximity.vending.domain.exception.NotFoundEntityException;
import com.proximity.vending.domain.model.Product;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.persistence.entities.ProductEntity;
import com.proximity.vending.persistence.mapper.ProductDBMapper;
import com.proximity.vending.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryDBTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static List<ProductEntity> PRODUCT_ENTITIES = new ArrayList<>();

    @Mock
    private ProductJpaRepository productJpaRepository;

    @Mock
    private ProductDBMapper productDBMapper;

    @InjectMocks
    private ProductRepositoryDB productRepository;

    @BeforeAll
    public static void initAll() throws IOException {
        Path jsonPath = Paths.get("src/test/resources/products.json");
        PRODUCT_ENTITIES = OBJECT_MAPPER.readValue(jsonPath.toFile(), new TypeReference<List<ProductEntity>>() {});
    }

    @Test
    public void testSuccessFindAll() {
        when(productJpaRepository.findAll())
                .thenReturn(PRODUCT_ENTITIES);
        when(productDBMapper.map(any(ProductEntity.class))).thenCallRealMethod();

        Set<Product> products = this.productRepository.findAllProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    public void testFindByID() {
        ProductID productID = ProductID.of("P001");
        when(productJpaRepository.findByCode(eq(productID.getValue())))
                .thenReturn(PRODUCT_ENTITIES.stream()
                        .filter(entity -> ProductID.of(entity.getCode()).equals(productID))
                        .findFirst());
        when(productDBMapper.map(any(ProductEntity.class))).thenCallRealMethod();

        Product product = this.productRepository.findByProductID(productID);

        assertNotNull(product);
        assertEquals(productID, product.getProductID());
    }

    @Test
    public void testNotFound() {
        ProductID productID = ProductID.of("P005");
        when(productJpaRepository.findByCode(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> productRepository.findByProductID(productID));
    }

    @Test
    public void testCreate() {
        int id = 5;
        String code = "P005";
        String name = "New Product";
        BigDecimal price = BigDecimal.valueOf(2.0);
        Product product = Product.builder()
                .code(code)
                .name(name)
                .price(price)
                .build();
        ProductEntity returnProductEntity = ProductEntity.builder()
                .id(id)
                .code(code)
                .name(name)
                .price(price)
                .build();
        when(productJpaRepository.save(any(ProductEntity.class)))
                .thenReturn(returnProductEntity);
        when(productDBMapper.map(any(ProductEntity.class))).thenCallRealMethod();
        when(productDBMapper.map(any(Product.class))).thenCallRealMethod();

        Product createdProduct = this.productRepository.createProduct(product);

        assertNotNull(createdProduct);
        assertEquals(code, createdProduct.getProductID().getValue());
        assertEquals(name, createdProduct.getName().getValue());
        assertTrue(price.compareTo(createdProduct.getPrice().getValue()) == 0);
    }

    @Test
    public void testUpdate() {
        int id = 4;
        String code = "P004";
        String name = "Updated Product";
        BigDecimal price = BigDecimal.valueOf(1.5);
        Product product = Product.builder()
                .code(code)
                .name(name)
                .price(price)
                .build();
        ProductEntity returnProductEntity = ProductEntity.builder()
                .id(id)
                .code(code)
                .name(name)
                .price(price)
                .build();

        when(this.productJpaRepository.findByCode(code))
                .thenReturn(PRODUCT_ENTITIES.stream()
                        .filter(entity -> entity.getId() == id)
                        .findFirst());
        when(productJpaRepository.save(any(ProductEntity.class)))
                .thenReturn(returnProductEntity);
        when(this.productDBMapper.map(any(ProductEntity.class))).thenCallRealMethod();
        when(this.productDBMapper.map(any(Product.class))).thenCallRealMethod();

        Product updatedProduct = this.productRepository.updateProduct(product);

        assertNotNull(updatedProduct);
        assertEquals(code, updatedProduct.getProductID().getValue());
        assertEquals(name, updatedProduct.getName().getValue());
        assertTrue(price.compareTo(updatedProduct.getPrice().getValue()) == 0);
    }
}

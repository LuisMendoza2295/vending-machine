package com.proximity.vending.admin.service.impl;

import com.proximity.vending.admin.dto.ProductDTO;
import com.proximity.vending.admin.service.ProductService;
import com.proximity.vending.domain.model.Product;
import com.proximity.vending.domain.repository.ProductRepository;
import com.proximity.vending.domain.vo.ProductID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Set<Product> findAll() {
        return this.productRepository.findAllProducts();
    }

    @Override
    public Product findByProductID(String productID) {
        return this.productRepository.findByProductID(ProductID.of(productID));
    }

    @Override
    public Product createProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .code(productDTO.getCode())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .build();
        log.info("PRODUCT TO BE CREATED {}", product);

        Product savedProduct = this.productRepository.createProduct(product);
        log.info("SAVED PRODUCT {}", savedProduct);

        return savedProduct;
    }

    @Override
    public Product changePrice(String productID, BigDecimal price) {
        Product product = this.productRepository.findByProductID(ProductID.of(productID));
        log.info("CURRENT PRODUCT {}", product);

        Product updatedProduct = this.productRepository.updateProduct(product.toBuilder()
                .price(price)
                .build());
        log.info("UPDATED PRODUCT {}", updatedProduct);

        return updatedProduct;
    }
}

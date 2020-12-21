package com.proximity.vending.persistence.mapper;

import com.proximity.vending.domain.model.Product;
import com.proximity.vending.persistence.entities.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductDBMapper {

    public Product map(ProductEntity productEntity) {
        return Product.builder()
                .code(productEntity.getCode())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .build();
    }

    public ProductEntity map(Product product) {
        return ProductEntity.builder()
                .code(product.getProductID().getValue())
                .name(product.getName().getValue())
                .price(product.getPrice().getValue())
                .build();
    }
}

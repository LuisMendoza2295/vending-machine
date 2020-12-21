package com.proximity.vending.admin.mapper;

import com.proximity.vending.admin.dto.ProductDTO;
import com.proximity.vending.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOMapper {

    public ProductDTO map(Product product) {
        return ProductDTO.builder()
                .code(product.getProductID().getValue())
                .name(product.getName().getValue())
                .price(product.getPrice().getValue())
                .build();
    }
}

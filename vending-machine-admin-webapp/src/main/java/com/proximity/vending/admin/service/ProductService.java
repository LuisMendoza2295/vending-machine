package com.proximity.vending.admin.service;

import com.proximity.vending.admin.dto.ProductDTO;
import com.proximity.vending.domain.model.Product;

import java.math.BigDecimal;
import java.util.Set;

public interface ProductService {

    Set<Product> findAll();

    Product findByProductID(String productID);

    Product createProduct(ProductDTO productDTO);

    Product changePrice(String productID, BigDecimal price);
}

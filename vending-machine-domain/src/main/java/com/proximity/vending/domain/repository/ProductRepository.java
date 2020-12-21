package com.proximity.vending.domain.repository;

import com.proximity.vending.domain.model.Product;
import com.proximity.vending.domain.vo.ProductID;

import java.util.Set;

public interface ProductRepository {

    Set<Product> findAllProducts();

    Set<Product> findAllProducts(Set<ProductID> productIDs);

    Product findByProductID(ProductID productID);

    Product createProduct(Product product);

    Product updateProduct(Product product);
}

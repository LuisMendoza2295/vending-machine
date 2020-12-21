package com.proximity.vending.persistence.impl;

import com.proximity.vending.domain.model.Product;
import com.proximity.vending.domain.repository.ProductRepository;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.persistence.entities.ProductEntity;
import com.proximity.vending.persistence.mapper.ProductDBMapper;
import com.proximity.vending.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.EmptyStackException;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryDB implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    private final ProductDBMapper productDBMapper;

    @Override
    public Set<Product> findAllProducts() {
        return this.productJpaRepository.findAll()
                .stream()
                .map(this.productDBMapper::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Product> findAllProducts(Set<ProductID> productIDs) {
        return this.productJpaRepository
                .findAllByCodes(productIDs.stream().map(ProductID::getValue).collect(Collectors.toList()))
                .stream()
                .map(this.productDBMapper::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Product findByProductID(ProductID productID) {
        ProductEntity productEntity = this.productJpaRepository.findByCode(productID.getValue())
                .orElseThrow(EmptyStackException::new);

        return this.productDBMapper.map(productEntity);
    }

    @Override
    public Product createProduct(Product product) {
        ProductEntity productEntity = this.productDBMapper.map(product);

        ProductEntity savedProductEntity = this.productJpaRepository.save(productEntity);

        return this.productDBMapper.map(savedProductEntity);
    }

    @Override
    public Product updateProduct(Product product) {
        ProductEntity currentProductEntity = this.productJpaRepository.findByCode(product.getProductID().getValue())
                .orElseThrow(EmptyStackException::new);

        ProductEntity productEntity = this.productDBMapper.map(product);
        productEntity.setId(currentProductEntity.getId());

        ProductEntity updatedProductEntity = this.productJpaRepository.save(productEntity);
        return this.productDBMapper.map(updatedProductEntity);
    }
}

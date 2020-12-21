package com.proximity.vending.persistence.repository;

import com.proximity.vending.persistence.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = "" +
            "SELECT p " +
            "FROM ProductEntity p " +
            "WHERE p.code IN :productCodes")
    Set<ProductEntity> findAllByCodes(@Param("productCodes") List<String> productCodes);

    @Query(value = "" +
            "SELECT p " +
            "FROM ProductEntity p " +
            "WHERE p.code = :code")
    Optional<ProductEntity> findByCode(@Param("code") String code);
}

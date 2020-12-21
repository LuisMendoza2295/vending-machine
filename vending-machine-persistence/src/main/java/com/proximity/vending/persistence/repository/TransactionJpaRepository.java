package com.proximity.vending.persistence.repository;

import com.proximity.vending.persistence.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    @Query(value = "" +
            "SELECT t " +
            "FROM TransactionEntity t " +
            "WHERE t.vendingMachineEntity.code = :vendingMachineID " +
            "AND t.time >= :from AND t.time <= :to")
    List<TransactionEntity> findAllByVendingMachineBetweenDates(@Param("vendingMachineID") String vendingMachineIDID,
                                                                @Param("from") LocalDateTime from,
                                                                @Param("to") LocalDateTime to);
}

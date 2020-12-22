package com.proximity.vending.persistence.repository;

import com.proximity.vending.persistence.entities.TransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    @Query(value = "" +
            "SELECT t " +
            "FROM TransactionEntity t " +
            "WHERE t.vendingMachineEntity.code = :vendingMachineID " +
            "AND t.time >= :from AND t.time <= :to " +
            "ORDER BY t.time DESC")
    List<TransactionEntity> findAllByVendingMachineBetweenDates(@Param("vendingMachineID") String vendingMachineIDID,
                                                                @Param("from") LocalDateTime from,
                                                                @Param("to") LocalDateTime to);

    @Query(value = "" +
            "SELECT t " +
            "FROM TransactionEntity t " +
            "WHERE t.time >= :from AND t.time <= :to " +
            "ORDER BY t.time DESC")
    List<TransactionEntity> findAllBetweenDates(@Param("from") LocalDateTime from,
                                                @Param("to") LocalDateTime to,
                                                Pageable pageable);

    @Query(value = "" +
            "SELECT SUM(t.amount) " +
            "FROM TransactionEntity t " +
            "WHERE t.time >= :from AND t.time <= :to")
    BigDecimal getTransactionAmountSum(@Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to);
}

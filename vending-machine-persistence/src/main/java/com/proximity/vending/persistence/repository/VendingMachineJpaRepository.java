package com.proximity.vending.persistence.repository;

import com.proximity.vending.persistence.entities.VendingMachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendingMachineJpaRepository extends JpaRepository<VendingMachineEntity, Long> {

    @Query(value = "" +
            "SELECT vm " +
            "FROM VendingMachineEntity vm " +
            "WHERE vm.code = :code")
    Optional<VendingMachineEntity> findByCode(@Param("code") String code);

    @Modifying
    @Query(value = "" +
            "UPDATE VendingMachineEntity vm " +
            "SET vm.accessAttempts = 0, vm.status = :unlockStatus " +
            "WHERE vm.accessAttempts > 0 OR vm.status = :lockStatus")
    int unlockVendingMachines(@Param("lockStatus") String lockStatus, @Param("unlockStatus") String unlockStatus);

    @Modifying
    @Query(value = "" +
            "UPDATE VendingMachineEntity vm " +
            "SET vm.accessAttempts = 0, vm.status = :unlockStatus " +
            "WHERE vm.code = :code")
    int unlockVendingMachine(@Param("code") String code, @Param("unlockStatus") String unlockStatus);
}

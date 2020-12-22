package com.proximity.vending.persistence.impl;

import com.proximity.vending.domain.exception.NotFoundEntityException;
import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.repository.AlertRepository;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.vo.VendingMachineID;
import com.proximity.vending.persistence.entities.VendingMachineEntity;
import com.proximity.vending.persistence.repository.VendingMachineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class AlertRepositoryDB implements AlertRepository {

    private final VendingMachineJpaRepository vendingMachineJpaRepository;

    @Override
    public void sendMoneyPickUpAlert(VendingMachineID vendingMachineID) {
        VendingMachineEntity vendingMachineEntity = this.vendingMachineJpaRepository.findByCode(vendingMachineID.getValue())
                .orElseThrow(() -> new NotFoundEntityException(VendingMachine.class));

        vendingMachineEntity.setStatus(VendingMachineStatus.FOR_MONEY_PICKUP.getCode());

        this.vendingMachineJpaRepository.save(vendingMachineEntity);
    }
}

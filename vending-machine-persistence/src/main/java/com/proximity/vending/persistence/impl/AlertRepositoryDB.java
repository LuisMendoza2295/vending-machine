package com.proximity.vending.persistence.impl;

import com.proximity.vending.domain.repository.AlertRepository;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.vo.VendingMachineID;
import com.proximity.vending.persistence.entities.VendingMachineEntity;
import com.proximity.vending.persistence.repository.VendingMachineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.EmptyStackException;

@Repository
@RequiredArgsConstructor
public class AlertRepositoryDB implements AlertRepository {

    private final VendingMachineJpaRepository vendingMachineJpaRepository;

    @Override
    public void sendMoneyPickUpAlert(VendingMachineID vendingMachineID) {
        VendingMachineEntity vendingMachineEntity = this.vendingMachineJpaRepository.findByCode(vendingMachineID.getValue())
                .orElseThrow(EmptyStackException::new);

        vendingMachineEntity.setStatus(VendingMachineStatus.FOR_MONEY_PICKUP.getCode());

        this.vendingMachineJpaRepository.save(vendingMachineEntity);
    }
}

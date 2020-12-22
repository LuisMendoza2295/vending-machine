package com.proximity.vending.persistence.impl;

import com.proximity.vending.domain.exception.NotFoundEntityException;
import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.repository.VendingMachineRepository;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.VendingMachineAccessCode;
import com.proximity.vending.domain.vo.VendingMachineID;
import com.proximity.vending.persistence.entities.ProductEntity;
import com.proximity.vending.persistence.entities.VaultEntity;
import com.proximity.vending.persistence.entities.VendingMachineEntity;
import com.proximity.vending.persistence.entities.VendingMachineProductEntity;
import com.proximity.vending.persistence.mapper.VendingMachineDBMapper;
import com.proximity.vending.persistence.repository.ProductJpaRepository;
import com.proximity.vending.persistence.repository.VendingMachineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VendingMachineRepositoryDB implements VendingMachineRepository {

    private final VendingMachineJpaRepository vendingMachineJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    private final VendingMachineDBMapper vendingMachineDBMapper;

    @Override
    public VendingMachine findByVendingMachineID(VendingMachineID vendingMachineID) {
        VendingMachineEntity vendingMachineEntity = this.vendingMachineJpaRepository
                .findByCode(vendingMachineID.getValue())
                .orElseThrow(() -> new NotFoundEntityException(VendingMachine.class));

        return this.vendingMachineDBMapper.map(vendingMachineEntity);
    }

    @Override
    public VendingMachine createVendingMachine(VendingMachine vendingMachine) {
        Set<ProductEntity> productEntities = this.productJpaRepository.findAllByCodes(vendingMachine.getProducts()
                .keySet()
                .stream()
                .map(ProductID::getValue)
                .collect(Collectors.toList()));

        VendingMachineEntity vendingMachineEntity = this.vendingMachineDBMapper.map(vendingMachine, productEntities);

        VendingMachineEntity savedVendingMachineEntity = this.vendingMachineJpaRepository.save(vendingMachineEntity);

        return this.vendingMachineDBMapper.map(savedVendingMachineEntity);
    }

    @Override
    public VendingMachine updateVendingMachine(VendingMachine vendingMachine) {
        VendingMachineEntity currentVendingMachineEntity = this.vendingMachineJpaRepository.findByCode(vendingMachine.getVendingMachineID().getValue())
                .orElseThrow(() -> new NotFoundEntityException(VendingMachine.class));

        Set<ProductEntity> productEntities = this.productJpaRepository.findAllByCodes(vendingMachine.getProducts()
                .keySet()
                .stream()
                .map(ProductID::getValue)
                .collect(Collectors.toList()));

        VendingMachineEntity vendingMachineEntity = this.vendingMachineDBMapper.map(vendingMachine, productEntities);
        vendingMachineEntity.setId(currentVendingMachineEntity.getId());

        // Set the mapped VendingMachineEntity's (from VendingMachine param)
        // each MachineProduct detail IDs (from the current VendingMachineEntity)
        // only if found, otherwise it will be a new record for the VendingMachineEntity
        for (VendingMachineProductEntity newEntity : vendingMachineEntity.getVendingMachineProductEntities()) {
            currentVendingMachineEntity
                    .getVendingMachineProductEntities()
                    .stream()
                    .filter(current -> current.getVendingMachineEntity().getId() == newEntity.getVendingMachineEntity().getId()
                            && current.getProductEntity().getId() == newEntity.getProductEntity().getId())
                    .findFirst()
                    .ifPresent(current -> newEntity.setId(current.getId()));
        }

        // Set the mapped VendingMachineEntity's (from VendingMachine param)
        // each VaultEntity detail IDs (from the current VendingMachineEntity)
        // only if found, otherwise it will be a new record for the VendingMachineEntity
        for (VaultEntity newEntity : vendingMachineEntity.getVaultEntities()) {
            currentVendingMachineEntity
                    .getVaultEntities()
                    .stream()
                    .filter(current -> current.getVendingMachineEntity().getId() == newEntity.getVendingMachineEntity().getId()
                            && Denomination.fromCode(current.getCurrencyDenomination()).equals(Denomination.fromCode(newEntity.getCurrencyDenomination())))
                    .findFirst()
                    .ifPresent(current -> newEntity.setId(current.getId()));
        }

        VendingMachineEntity updatedVendingMachineEntity = this.vendingMachineJpaRepository.save(vendingMachineEntity);

        return this.vendingMachineDBMapper.map(updatedVendingMachineEntity);
    }

    @Override
    public boolean openVendingMachine(VendingMachineID vendingMachineID, VendingMachineAccessCode accessCode) {
        VendingMachineEntity vendingMachineEntity = this.vendingMachineJpaRepository.findByCode(vendingMachineID.getValue())
                .orElseThrow(() -> new NotFoundEntityException(VendingMachine.class));

        VendingMachineAccessCode currentAccessCode = VendingMachineAccessCode.of(vendingMachineEntity.getAccessCode());

        int attempts = vendingMachineEntity.getAccessAttempts();
        if (currentAccessCode.equals(accessCode)) {
            attempts = 0;
            vendingMachineEntity.setStatus(VendingMachineStatus.OPEN.getCode());
        } else {
            attempts++;
            VendingMachineStatus vendingMachineStatus = VendingMachineStatus.fromAttempts(attempts);
            vendingMachineEntity.setStatus(vendingMachineStatus.getCode());
        }

        vendingMachineEntity.setAccessAttempts(attempts);

        this.vendingMachineJpaRepository.save(vendingMachineEntity);

        return currentAccessCode.equals(accessCode);
    }

    @Override
    public VendingMachine changeStatus(VendingMachineID vendingMachineID, VendingMachineStatus vendingMachineStatus) {
        VendingMachineEntity vendingMachineEntity = this.vendingMachineJpaRepository.findByCode(vendingMachineID.getValue())
                .orElseThrow(() -> new NotFoundEntityException(VendingMachine.class));

        vendingMachineEntity.setStatus(vendingMachineStatus.getCode());

        VendingMachineEntity updatedVendingMachineEntity = this.vendingMachineJpaRepository.save(vendingMachineEntity);

        return this.vendingMachineDBMapper.map(updatedVendingMachineEntity);
    }

    @Override
    public int unlockVendingMachines() {
        return this.vendingMachineJpaRepository.unlockVendingMachines(VendingMachineStatus.BLOCKED.getCode(), VendingMachineStatus.OK.getCode());
    }

    @Override
    public boolean vendingMachinePing(VendingMachineID vendingMachineID) {
        VendingMachineEntity vendingMachineEntity = this.vendingMachineJpaRepository.findByCode(vendingMachineID.getValue())
                .orElseThrow(() -> new NotFoundEntityException(VendingMachine.class));

        LocalDateTime now = LocalDateTime.now();
        vendingMachineEntity.setLastPing(now);

        VendingMachineEntity updateVendingMachineEntity = this.vendingMachineJpaRepository.save(vendingMachineEntity);

        return updateVendingMachineEntity.getLastPing().equals(now);
    }
}

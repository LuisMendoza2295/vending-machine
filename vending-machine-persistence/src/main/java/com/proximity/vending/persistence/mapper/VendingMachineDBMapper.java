package com.proximity.vending.persistence.mapper;

import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.persistence.entities.ProductEntity;
import com.proximity.vending.persistence.entities.VendingMachineEntity;
import com.proximity.vending.persistence.entities.VendingMachineProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VendingMachineDBMapper {

    private final VaultEntityMapper vaultEntityMapper;

    public VendingMachine map(VendingMachineEntity vendingMachineEntity) {
        VendingMachine vendingMachine = VendingMachine.builder()
                .code(vendingMachineEntity.getCode())
                .accessCode(vendingMachineEntity.getAccessCode())
                .status(vendingMachineEntity.getStatus())
                .type(vendingMachineEntity.getType())
                .lastMoneyPickUp(vendingMachineEntity.getLastMoneyPickUp())
                .build();

        vendingMachineEntity.getVendingMachineProductEntities()
                .stream()
                .collect(
                        Collectors.toMap(
                                vendingMachineProductEntity -> ProductID.of(vendingMachineProductEntity.getProductEntity().getCode()),
                                VendingMachineProductEntity::getCount))
                .forEach(vendingMachine::putProduct);

        vendingMachineEntity.getVaultEntities()
                .forEach(vaultEntity -> {
                    Denomination denomination = Denomination.fromCode(vaultEntity.getCurrencyDenomination());
                    vendingMachine.putCurrencyCount(denomination, vaultEntity.getCount());
                });

        vendingMachineEntity.getVendingMachineProductEntities()
                .stream()
                .collect(
                        Collectors.toMap(
                                vendingMachineProductEntity -> ProductID.of(vendingMachineProductEntity.getProductEntity().getCode()),
                                vendingMachineProductEntity -> vendingMachineProductEntity.getProductEntity().getPrice()))
                .forEach(vendingMachine::putMoneyAmountsAsPrices);

        return vendingMachine;
    }

    public VendingMachineEntity map(VendingMachine vendingMachine, Set<ProductEntity> productEntities) {
        VendingMachineEntity vendingMachineEntity = VendingMachineEntity.builder()
                .code(vendingMachine.getVendingMachineID().getValue())
                .accessCode(vendingMachine.getAccessCode().getValue())
                .status(vendingMachine.getStatus().getCode())
                .type(vendingMachine.getType().getCode())
                .lastMoneyPickUp(vendingMachine.getLastMoneyPickUp())
                .vendingMachineProductEntities(new HashSet<>())
                .vaultEntities(new HashSet<>())
                .build();

        vendingMachine.getDenominationCount()
                .entrySet()
                .stream()
                .map(denominationEntry -> this.vaultEntityMapper.map(denominationEntry.getKey(), denominationEntry.getValue()))
                .forEach(vendingMachineEntity::addVaultEntity);

        vendingMachine.getProducts()
                .entrySet()
                .stream()
                .map(productCountEntry -> findAndAssign(productCountEntry.getKey(), productCountEntry.getValue(), productEntities))
                .forEach(vendingMachineEntity::addVendingMachineProductEntity);

        return vendingMachineEntity;
    }

    private VendingMachineProductEntity findAndAssign(ProductID productID, int count, Set<ProductEntity> productEntities) {
        ProductEntity productEntity = productEntities
                .stream()
                .filter(productEntityFilter -> ProductID.of(productEntityFilter.getCode()).equals(productID))
                .findFirst()
                .orElseThrow(EmptyStackException::new);

        return VendingMachineProductEntity.builder()
                .productEntity(productEntity)
                .count(count)
                .build();
    }
}

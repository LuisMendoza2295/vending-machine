package com.proximity.vending.admin.mapper;

import com.proximity.vending.admin.config.VendingMachineProperties;
import com.proximity.vending.admin.dto.VendingMachineDTO;
import com.proximity.vending.domain.model.VendingMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VendingMachineDTOMapper {

    private final VendingMachineProperties vendingMachineProperties;

    public VendingMachineDTO map(VendingMachine vendingMachine) {
        return VendingMachineDTO.builder()
                .code(vendingMachine.getVendingMachineID().getValue())
                .status(vendingMachine.getStatus().getCode())
                .type(vendingMachine.getType().getCode())
                .lastMoneyPickUp(vendingMachine.getLastMoneyPickUp())
                .connected(vendingMachine.isConnected(this.vendingMachineProperties.getPingThresholdMillis()))
                .products(vendingMachine.getProducts()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().getValue(), Map.Entry::getValue)))
                .vault(vendingMachine.getDenominationCount()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().getCode(), Map.Entry::getValue)))
                .prices(vendingMachine.getPrices()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().getValue(), e -> e.getValue().getValue())))
                .build();
    }
}

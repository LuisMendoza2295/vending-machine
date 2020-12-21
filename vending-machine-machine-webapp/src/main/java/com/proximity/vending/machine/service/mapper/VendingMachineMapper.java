package com.proximity.vending.machine.service.mapper;

import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.machine.service.dto.VendingMachineDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VendingMachineMapper {

    public VendingMachine map(VendingMachineDTO vendingMachineDTO) {
        VendingMachine vendingMachine = VendingMachine.builder()
                .code(vendingMachineDTO.getCode())
                .status(vendingMachineDTO.getStatus())
                .type(vendingMachineDTO.getType())
                .lastMoneyPickUp(vendingMachineDTO.getLastMoneyPickUp())
                .build();

        vendingMachineDTO.getProducts()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> ProductID.of(e.getKey()), Map.Entry::getValue))
                .forEach(vendingMachine::putProduct);

        vendingMachineDTO.getVault()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Denomination.fromCode(e.getKey()), Map.Entry::getValue))
                .forEach(vendingMachine::putCurrencyCount);

        vendingMachineDTO.getPrices()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> ProductID.of(e.getKey()), Map.Entry::getValue))
                .forEach(vendingMachine::putMoneyAmountsAsPrices);

        return vendingMachine;
    }
}

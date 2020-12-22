package com.proximity.vending.admin.service.impl;

import com.proximity.vending.admin.config.VendingMachineProperties;
import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.admin.dto.ProductCountDTO;
import com.proximity.vending.admin.dto.VendingMachineDTO;
import com.proximity.vending.admin.service.VendingMachineService;
import com.proximity.vending.domain.exception.BlockedMachineException;
import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.OpenMachineException;
import com.proximity.vending.domain.exception.Preconditions;
import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.repository.VendingMachineRepository;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.vo.ProductID;
import com.proximity.vending.domain.vo.VendingMachineAccessCode;
import com.proximity.vending.domain.vo.VendingMachineID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendingMachineServiceImpl implements VendingMachineService {

    private final VendingMachineRepository vendingMachineRepository;
    private final VendingMachineProperties vendingMachineProperties;

    @Override
    public VendingMachine findByVendingMachineID(String vendingMachineID) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("FOUND MACHINE {}", vendingMachine);

        return vendingMachine;
    }

    @Override
    public VendingMachine createVendingMachine(VendingMachineDTO vendingMachineDTO) {
        VendingMachine vendingMachine = VendingMachine.builder()
                .code(vendingMachineDTO.getCode())
                .accessCode(this.vendingMachineProperties.getDefaultAccessCode())
                .status(VendingMachineStatus.OK.getCode())
                .type(vendingMachineDTO.getType())
                .lastMoneyPickUp(LocalDateTime.now())
                .lastPing(LocalDateTime.now())
                .build();
        log.info("MACHINE TO BE CREATED {}", vendingMachine);

        return this.vendingMachineRepository.createVendingMachine(vendingMachine);
    }

    @Override
    public VendingMachine addProduct(String vendingMachineID, ProductCountDTO productCountDTO) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CURRENT MACHINE {}", vendingMachine);

        productCountDTO.getProductCount()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> ProductID.of(e.getKey()), Map.Entry::getValue))
                .forEach(vendingMachine::putProduct);
        log.info("MACHINE TO BE UPDATED WITH ADDED PRODUCTS {}", vendingMachine);

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.updateVendingMachine(vendingMachine);
        log.info("UPDATED MACHINE {}", updatedVendingMachine);

        return updatedVendingMachine;
    }

    @Override
    public VendingMachine removeProduct(String vendingMachineID, String productID) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CURRENT MACHINE {}", vendingMachine);

        vendingMachine.removeProduct(ProductID.of(productID));
        log.info("MACHINE TO BE UPDATED WITH REMOVED PRODUCT {}", vendingMachine);

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.updateVendingMachine(vendingMachine);
        log.info("UPDATED MACHINE {}", updatedVendingMachine);

        return updatedVendingMachine;
    }

    @Override
    public VendingMachine changeAccessCode(String vendingMachineID, String accessCode) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CURRENT MACHINE {}", vendingMachine);

        VendingMachine toUpdateVendingMachine = vendingMachine.toBuilder()
                .accessCode(accessCode)
                .build();
        vendingMachine.getProducts()
                .forEach(toUpdateVendingMachine::putProduct);
        vendingMachine.getDenominationCount()
                .forEach(toUpdateVendingMachine::putCurrencyCount);
        log.info("MACHINE TO BE UPDATED WITH CHANGED ACCESS CODE {}", toUpdateVendingMachine);

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.updateVendingMachine(toUpdateVendingMachine);
        log.info("UPDATED MACHINE {}", updatedVendingMachine);

        return updatedVendingMachine;
    }

    @Override
    public VendingMachine updateVault(String vendingMachineID, DenominationCountDTO denominationCountDTO) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CURRENT MACHINE {}", vendingMachine);

        denominationCountDTO.getDenominationCount()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Denomination.fromCode(e.getKey()), Map.Entry::getValue))
                .forEach(vendingMachine::putCurrencyCount);
        log.info("MACHINE TO BE UPDATED WITH VAULT UPDATED {}", vendingMachine);

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.updateVendingMachine(vendingMachine);
        log.info("UPDATED MACHINE {}", updatedVendingMachine);

        return updatedVendingMachine;
    }

    @Override
    public boolean openVendingMachine(String vendingMachineID, String accessCode) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CURRENT MACHINE {}", vendingMachine);
        Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED), () -> new BlockedMachineException(vendingMachine.getVendingMachineID()));
        Preconditions.checkArgument(vendingMachine.getStatus().equals(VendingMachineStatus.OPEN), () -> new OpenMachineException(vendingMachine.getVendingMachineID()));

        log.info("TRYING TO OPEN MACHINE {}", vendingMachine.getVendingMachineID());
        return this.vendingMachineRepository.openVendingMachine(VendingMachineID.of(vendingMachineID), VendingMachineAccessCode.of(accessCode));
    }

    @Override
    public boolean closeVendingMachine(String vendingMachineID) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CURRENT MACHINE {}", vendingMachine);
        Preconditions.checkNotArgument(vendingMachine.getStatus().equals(VendingMachineStatus.OPEN), () -> new OpenMachineException(vendingMachine.getVendingMachineID()));

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.changeStatus(VendingMachineID.of(vendingMachineID), VendingMachineStatus.OK);
        log.info("UPDATED MACHINE {}", updatedVendingMachine);

        return updatedVendingMachine.getStatus().equals(VendingMachineStatus.OK);
    }

    @Override
    public boolean unlockVendingMachine(String vendingMachineID) {
        VendingMachine vendingMachine = this.vendingMachineRepository.findByVendingMachineID(VendingMachineID.of(vendingMachineID));
        log.info("CURRENT MACHINE {}", vendingMachine);
        Preconditions.checkNotArgument(vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED), () -> new BlockedMachineException(vendingMachine.getVendingMachineID()));

        VendingMachine updatedVendingMachine = this.vendingMachineRepository.changeStatus(VendingMachineID.of(vendingMachineID), VendingMachineStatus.OK);
        log.info("UPDATED MACHINE {}", updatedVendingMachine);

        return updatedVendingMachine.getStatus().equals(VendingMachineStatus.OK);
    }

    @Override
    public boolean vendingMachinePing(String vendingMachineID) {
        log.info("PING FROM MACHINE {}", vendingMachineID);
        return this.vendingMachineRepository.vendingMachinePing(VendingMachineID.of(vendingMachineID));
    }
}

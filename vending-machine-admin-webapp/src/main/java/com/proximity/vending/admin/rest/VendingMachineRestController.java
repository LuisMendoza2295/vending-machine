package com.proximity.vending.admin.rest;

import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.admin.dto.ProductCountDTO;
import com.proximity.vending.admin.dto.VendingMachineDTO;
import com.proximity.vending.admin.mapper.VendingMachineDTOMapper;
import com.proximity.vending.admin.service.VendingMachineService;
import com.proximity.vending.domain.model.VendingMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vending-machine")
@RequiredArgsConstructor
public class VendingMachineRestController {

    private final VendingMachineService vendingMachineService;

    private final VendingMachineDTOMapper vendingMachineDTOMapper;

    @GetMapping("/{code}")
    public VendingMachineDTO findByCode(@PathVariable("code") String code) {
        VendingMachine vendingMachine = this.vendingMachineService.findByVendingMachineID(code);

        return this.vendingMachineDTOMapper.map(vendingMachine);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendingMachineDTO createVendingMachine(@RequestBody VendingMachineDTO vendingMachineDTO) {
        VendingMachine createdVendingMachine = this.vendingMachineService.createVendingMachine(vendingMachineDTO);

        return this.vendingMachineDTOMapper.map(createdVendingMachine);
    }

    @PatchMapping("/{code}/put-products")
    public VendingMachineDTO putProducts(@PathVariable("code") String code, @RequestBody ProductCountDTO countDTO) {
        VendingMachine vendingMachine = this.vendingMachineService.putProducts(code, countDTO);

        return this.vendingMachineDTOMapper.map(vendingMachine);
    }

    @PatchMapping("/{code}/remove-product")
    public VendingMachineDTO removeProduct(@PathVariable("code") String code, @RequestParam String productCode) {
        VendingMachine vendingMachine = this.vendingMachineService.removeProduct(code, productCode);

        return this.vendingMachineDTOMapper.map(vendingMachine);
    }

    @PatchMapping("/{code}/vault")
    public VendingMachineDTO updateVault(@PathVariable("code") String code, @RequestBody DenominationCountDTO countDTO) {
        VendingMachine vendingMachine = this.vendingMachineService.updateVault(code, countDTO);

        return this.vendingMachineDTOMapper.map(vendingMachine);
    }

    @PatchMapping("/{code}/access-code")
    public VendingMachineDTO changeAccessCode(@PathVariable("code") String code, @RequestParam("accessCode") String accessCode) {
        VendingMachine vendingMachine = this.vendingMachineService.changeAccessCode(code, accessCode);

        return this.vendingMachineDTOMapper.map(vendingMachine);
    }

    @PostMapping("/{code}/open")
    public boolean accessMachine(@PathVariable("code") String code, @RequestParam("accessCode") String accessCode) {
        return this.vendingMachineService.openVendingMachine(code, accessCode);
    }

    @PostMapping("/{code}/close")
    public boolean closeMachine(@PathVariable("code") String code) {
        return this.vendingMachineService.closeVendingMachine(code);
    }

    @PostMapping("/{code}/unlock")
    public boolean unlockAccessMachine(@PathVariable("code") String code) {
        return this.vendingMachineService.unlockVendingMachine(code);
    }

    @PostMapping("/{code}/ping")
    public boolean pingMachine(@PathVariable("code") String code) {
        return this.vendingMachineService.vendingMachinePing(code);
    }
}

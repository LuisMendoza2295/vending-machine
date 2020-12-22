package com.proximity.vending.admin.service;

import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.admin.dto.ProductCountDTO;
import com.proximity.vending.admin.dto.VendingMachineDTO;
import com.proximity.vending.domain.model.VendingMachine;

public interface VendingMachineService {

    VendingMachine findByVendingMachineID(String vendingMachineID);

    VendingMachine createVendingMachine(VendingMachineDTO vendingMachineDTO);

    VendingMachine putProducts(String vendingMachineID, ProductCountDTO productCountDTO);

    VendingMachine removeProduct(String vendingMachineID, String productID);

    VendingMachine changeAccessCode(String vendingMachineID, String accessCode);

    VendingMachine updateVault(String vendingMachineID, DenominationCountDTO denominationCountDTO);

    boolean openVendingMachine(String vendingMachineID, String accessCode);

    boolean closeVendingMachine(String vendingMachineID);

    boolean unlockVendingMachine(String vendingMachineID);

    boolean vendingMachinePing(String vendingMachineID);
}

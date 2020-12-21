package com.proximity.vending.domain.repository;

import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.vo.VendingMachineAccessCode;
import com.proximity.vending.domain.vo.VendingMachineID;

public interface VendingMachineRepository {

    VendingMachine findByVendingMachineID(VendingMachineID vendingMachineID);

    VendingMachine createVendingMachine(VendingMachine vendingMachine);

    VendingMachine updateVendingMachine(VendingMachine vendingMachine);

    boolean openVendingMachine(VendingMachineID vendingMachineID, VendingMachineAccessCode accessCode);

    boolean unlockVendingMachine(VendingMachineID vendingMachineID);

    int unlockVendingMachines();
}

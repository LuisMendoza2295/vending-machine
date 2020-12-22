package com.proximity.vending.domain.repository;

import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.type.VendingMachineStatus;
import com.proximity.vending.domain.vo.VendingMachineAccessCode;
import com.proximity.vending.domain.vo.VendingMachineID;

public interface VendingMachineRepository {

    VendingMachine findByVendingMachineID(VendingMachineID vendingMachineID);

    VendingMachine createVendingMachine(VendingMachine vendingMachine);

    VendingMachine updateVendingMachine(VendingMachine vendingMachine);

    boolean openVendingMachine(VendingMachineID vendingMachineID, VendingMachineAccessCode accessCode);

    VendingMachine changeStatus(VendingMachineID vendingMachineID, VendingMachineStatus vendingMachineStatus);

    boolean vendingMachinePing(VendingMachineID vendingMachineID);

    int unlockVendingMachines();
}

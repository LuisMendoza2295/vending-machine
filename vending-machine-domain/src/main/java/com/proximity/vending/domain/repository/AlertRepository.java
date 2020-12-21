package com.proximity.vending.domain.repository;

import com.proximity.vending.domain.vo.VendingMachineID;

public interface AlertRepository {

    void sendMoneyPickUpAlert(VendingMachineID vendingMachineID);
}

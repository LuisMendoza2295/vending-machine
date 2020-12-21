package com.proximity.vending.admin.service.event;

import com.proximity.vending.domain.model.VendingMachine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class VendingMachineUpdatedEvent {

    private VendingMachine vendingMachine;
}

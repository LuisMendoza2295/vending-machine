package com.proximity.vending.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VendingMachineTest {

    private VendingMachine vendingMachine;

    @BeforeEach
    public void initEach() {
        vendingMachine = VendingMachine.builder()
                .build();
    }
}

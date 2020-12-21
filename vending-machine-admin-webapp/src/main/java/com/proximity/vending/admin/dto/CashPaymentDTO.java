package com.proximity.vending.admin.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CashPaymentDTO {

    private String productID;
    private String vendingMachineID;
    private Map<String, Integer> insertedDenomination;
}

package com.proximity.vending.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CardPaymentDTO {

    private String productID;
    private String vendingMachineID;
    private String issuer;
}

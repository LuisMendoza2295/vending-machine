package com.proximity.vending.machine.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ReceiptDTO {

    private String issuer;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String product;
    private String vendingMachine;
}

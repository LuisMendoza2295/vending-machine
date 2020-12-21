package com.proximity.vending.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {

    private String transactionID;
    private String productID;
    private String vendingMachineID;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String type;
    private String issuer;
    private Map<String, Integer> insertedDenomination;
}

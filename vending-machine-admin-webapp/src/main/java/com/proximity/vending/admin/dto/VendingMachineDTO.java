package com.proximity.vending.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendingMachineDTO {

    private String code;
    private String status;
    private String type;
    private LocalDateTime lastMoneyPickUp;
    private boolean connected;
    private Map<String, Integer> products = new HashMap<>();
    private Map<String, BigDecimal> prices = new HashMap<>();
    private Map<String, Integer> vault = new HashMap<>();
}

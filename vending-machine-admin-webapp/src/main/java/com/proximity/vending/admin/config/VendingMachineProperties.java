package com.proximity.vending.admin.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "vending.machine")
public class VendingMachineProperties {

    private String defaultAccessCode;
    private BigDecimal pickupThreshold;
    private long pingThresholdMillis;
}

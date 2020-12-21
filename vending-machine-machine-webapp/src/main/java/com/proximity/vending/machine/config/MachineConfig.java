package com.proximity.vending.machine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class MachineConfig {

    private final VendingMachineProperties machineProperties;

    @Bean
    public WebClient adminWebClient() {
        return WebClient.create(machineProperties.getAdminUrl());
    }
}

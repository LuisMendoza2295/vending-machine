package com.proximity.vending.machine.scheduled;

import com.proximity.vending.domain.exception.InvalidDataException;
import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.machine.config.VendingMachineProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class VendingMachinePingUpdater {

    private static final String SEND_PING_PATH = "/vending-machine/{code}/ping";

    private final WebClient adminWebClient;
    private final VendingMachineProperties machineProperties;

    @Scheduled(fixedRate = 60000)
    public void unlockVendingMachines() {
        log.info("SENDING PING TO {}", this.machineProperties.getAdminUrl());
        Mono<Boolean> pingMono = this.adminWebClient
                .post()
                .uri(SEND_PING_PATH, this.machineProperties.getId())
                .retrieve()
                .onStatus(
                        httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> clientResponse.body(BodyExtractors.toMono(BaseException.class)).flatMap(Mono::error)
                )
                .bodyToMono(Boolean.class)
                .switchIfEmpty(Mono.error(new InvalidDataException(VendingMachine.class)));

        pingMono.subscribe(
                ping -> log.info("PING SENT SUCCESSFULLY"),
                error -> log.error("ERROR SENDING PING"));
    }
}

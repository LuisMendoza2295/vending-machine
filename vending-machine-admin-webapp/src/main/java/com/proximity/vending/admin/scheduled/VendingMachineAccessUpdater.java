package com.proximity.vending.admin.scheduled;

import com.proximity.vending.domain.repository.VendingMachineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class VendingMachineAccessUpdater {

    private final VendingMachineRepository vendingMachineRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void unlockVendingMachines() {
        log.info("UNLOCK MACHINES AT {}", LocalDateTime.now());
        int updatedMachines = this.vendingMachineRepository.unlockVendingMachines();

        log.info("{} MACHINES UNLOCKED", updatedMachines);
    }
}

package com.proximity.vending.admin.service.event;

import com.proximity.vending.admin.config.VendingMachineProperties;
import com.proximity.vending.domain.model.VendingMachine;
import com.proximity.vending.domain.repository.AlertRepository;
import com.proximity.vending.domain.type.VendingMachineStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class VendingMachineUpdatedListener {

    private final AlertRepository alertRepository;
    private final VendingMachineProperties vendingMachineProperties;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVendingMachineUpdated(VendingMachineUpdatedEvent event) {
        log.info("VENDING MACHINE UPDATED EVENT {}", event);

        VendingMachine vendingMachine = event.getVendingMachine();
        if  (vendingMachine.getStatus().equals(VendingMachineStatus.FOR_MONEY_PICKUP)
                || vendingMachine.getStatus().equals(VendingMachineStatus.BLOCKED)) {
            log.info("VENDING MACHINE ALREADY IN STATUS {}", vendingMachine.getStatus());
            return;
        }

        if (vendingMachine.isOverMoneyPickupThreshold(this.vendingMachineProperties.getPickupThreshold())) {
            log.info("VENDING MACHINE OVER PICKUP THRESHOLD");
            this.alertRepository.sendMoneyPickUpAlert(vendingMachine.getVendingMachineID());
            log.info("VENDING MACHINE ALERT SENT");
        }
    }
}

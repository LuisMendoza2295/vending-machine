package com.proximity.vending.machine.rest;

import com.proximity.vending.machine.dto.CardDTO;
import com.proximity.vending.machine.dto.CashDTO;
import com.proximity.vending.machine.dto.ChangeDTO;
import com.proximity.vending.machine.dto.ReceiptDTO;
import com.proximity.vending.machine.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class MachineRestController {

    private final MachineService machineService;

    @PostMapping("/cash")
    public Mono<ChangeDTO> cashTransaction(@RequestBody CashDTO cashDTO) {
        return this.machineService.cashTransaction(cashDTO);
    }

    @PostMapping("/card")
    public Mono<ReceiptDTO> cardTransaction(@RequestBody CardDTO cardDTO) {
        return this.machineService.cardTransaction(cardDTO);
    }

    @PostMapping("/open")
    public Mono<Boolean> openMachine(@RequestParam("accessCode") String accessCode) {
        return this.machineService.openMachine(accessCode);
    }

    @PostMapping("/close")
    public Mono<Boolean> closeMachine() {
        return this.machineService.closeMachine();
    }
}

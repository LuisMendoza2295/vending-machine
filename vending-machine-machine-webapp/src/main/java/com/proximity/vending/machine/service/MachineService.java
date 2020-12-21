package com.proximity.vending.machine.service;

import com.proximity.vending.machine.dto.CardDTO;
import com.proximity.vending.machine.dto.CashDTO;
import com.proximity.vending.machine.dto.ChangeDTO;
import com.proximity.vending.machine.dto.ReceiptDTO;
import reactor.core.publisher.Mono;

public interface MachineService {

    Mono<ReceiptDTO> cardTransaction(CardDTO cardDTO);

    Mono<ChangeDTO> cashTransaction(CashDTO cashDTO);
}

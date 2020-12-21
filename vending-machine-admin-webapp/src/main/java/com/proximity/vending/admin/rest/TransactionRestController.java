package com.proximity.vending.admin.rest;

import com.proximity.vending.admin.dto.CardPaymentDTO;
import com.proximity.vending.admin.dto.CashPaymentDTO;
import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.admin.dto.TransactionDTO;
import com.proximity.vending.admin.mapper.TransactionDTOMapper;
import com.proximity.vending.admin.service.TransactionService;
import com.proximity.vending.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    private final TransactionDTOMapper transactionDTOMapper;

    @PostMapping("/cash")
    public TransactionDTO cashTransaction(@RequestBody CashPaymentDTO cashPaymentDTO) {
        Transaction transaction = this.transactionService.createCashTransaction(
                cashPaymentDTO.getProductID(),
                cashPaymentDTO.getVendingMachineID(),
                DenominationCountDTO.builder()
                        .denominationCount(cashPaymentDTO.getInsertedDenomination())
                        .build());

        return this.transactionDTOMapper.map(transaction);
    }

    @PostMapping("/card")
    public TransactionDTO cardTransaction(@RequestBody CardPaymentDTO cardPaymentDTO) {
        Transaction transaction = this.transactionService.createCardTransaction(
                cardPaymentDTO.getProductID(),
                cardPaymentDTO.getVendingMachineID(),
                cardPaymentDTO.getIssuer());

        return this.transactionDTOMapper.map(transaction);
    }
}

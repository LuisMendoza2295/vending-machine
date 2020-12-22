package com.proximity.vending.admin.rest;

import com.proximity.vending.admin.dto.CardPaymentDTO;
import com.proximity.vending.admin.dto.CashPaymentDTO;
import com.proximity.vending.admin.dto.DenominationCountDTO;
import com.proximity.vending.admin.dto.TransactionDTO;
import com.proximity.vending.admin.mapper.TransactionDTOMapper;
import com.proximity.vending.admin.service.TransactionService;
import com.proximity.vending.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    private final TransactionDTOMapper transactionDTOMapper;

    @GetMapping("/earnings")
    public BigDecimal totalEarnings(@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromParam,
                                    @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toParam) {
        LocalDateTime from = fromParam.atStartOfDay();
        LocalDateTime to = toParam.plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .minus(1, ChronoUnit.MILLIS);
        return this.transactionService.getTotalEarnings(from, to);
    }

    @GetMapping
    public List<TransactionDTO> findAllBetweenDates(@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromParam,
                                                    @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toParam,
                                                    @RequestParam("pageNumber") int pageNumber,
                                                    @RequestParam("pageSize") int pageSize) {
        LocalDateTime from = fromParam.atStartOfDay();
        LocalDateTime to = toParam.plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .minus(1, ChronoUnit.MILLIS);
        return this.transactionService.findAllBetweenDates(from, to, pageNumber, pageSize)
                .stream()
                .map(this.transactionDTOMapper::map)
                .collect(Collectors.toList());
    }

    @PostMapping("/cash")
    public TransactionDTO cashTransaction(@RequestBody CashPaymentDTO cashPaymentDTO) {
        Transaction transaction = this.transactionService.createCashTransaction(
                cashPaymentDTO.getProductID(),
                cashPaymentDTO.getVendingMachineID(),
                DenominationCountDTO.builder()
                        .denominationCount(cashPaymentDTO.getFinalDenominationCount())
                        .build(),
                DenominationCountDTO.builder()
                        .denominationCount(cashPaymentDTO.getInsertedDenominationCount())
                        .build()
        );

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

package com.proximity.vending.machine.dto;

import com.proximity.vending.domain.type.Denomination;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CashDTO {

    private String product;
    private Map<String, Integer> cash;

    public BigDecimal getPaidAmount() {
        return this.cash
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> Denomination.fromCode(e.getKey()), Map.Entry::getValue))
                .entrySet()
                .stream()
                .map(entry -> entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

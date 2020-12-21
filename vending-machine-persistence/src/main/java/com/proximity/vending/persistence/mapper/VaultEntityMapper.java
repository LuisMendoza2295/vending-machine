package com.proximity.vending.persistence.mapper;

import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.persistence.entities.VaultEntity;
import org.springframework.stereotype.Component;

@Component
public class VaultEntityMapper {

    public VaultEntity map(Denomination denomination, int count) {
        return VaultEntity.builder()
                .currencyDenomination(denomination.getCode())
                .currencyType(denomination.getCurrencyType().getCode())
                .count(count)
                .build();
    }
}

package com.proximity.vending.machine.service.mapper;

import com.proximity.vending.domain.model.Transaction;
import com.proximity.vending.domain.type.Denomination;
import com.proximity.vending.machine.dto.ChangeDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChangeDTOMapper {

    public ChangeDTO map(Transaction transaction, Map<Denomination, Integer> change) {
        return ChangeDTO.builder()
                .change(change
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> e.getKey().getCode(), Map.Entry::getValue)))
                .product(transaction.getProductID().getValue())
                .build();
    }
}

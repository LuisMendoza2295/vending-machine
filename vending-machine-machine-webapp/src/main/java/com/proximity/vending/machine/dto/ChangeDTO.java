package com.proximity.vending.machine.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ChangeDTO {

    private String product;
    private Map<String, Integer> change;
}

package com.proximity.vending.admin.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ProductCountDTO {

    private Map<String, Integer> productCount = new HashMap<>();
}

package com.proximity.vending.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public class NotFoundEntityException extends RuntimeException {

    private final Class<?> clazz;
    private final String message;

    public NotFoundEntityException(Class<?> clazz) {
        this.clazz = clazz;
        this.message = StringUtils.EMPTY;
    }
}

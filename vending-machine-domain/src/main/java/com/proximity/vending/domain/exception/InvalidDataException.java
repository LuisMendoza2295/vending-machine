package com.proximity.vending.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public class InvalidDataException extends RuntimeException {

    private final Object value;
    private final String message;
    private final Class<?> clazz;

    public InvalidDataException(Object value) {
        this.value = value;
        this.message = StringUtils.EMPTY;
        this.clazz = value.getClass();
    }
}

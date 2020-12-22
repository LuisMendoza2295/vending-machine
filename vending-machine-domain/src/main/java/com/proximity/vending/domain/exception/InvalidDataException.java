package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.exception.commons.ExceptionCodeConstants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class InvalidDataException extends BaseException {

    private final Object value;
    private final String message;
    private final Class<?> clazz;

    public InvalidDataException(Object value) {
        super(ExceptionCodeConstants.INVALID_DATA_EXCEPTION);
        this.value = value;
        this.message = StringUtils.EMPTY;
        this.clazz = value.getClass();
    }

    public InvalidDataException(Object value, String message, Class<?> clazz) {
        super(ExceptionCodeConstants.INVALID_DATA_EXCEPTION);
        this.value = value;
        this.message = message;
        this.clazz = clazz;
    }
}

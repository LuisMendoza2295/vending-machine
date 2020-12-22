package com.proximity.vending.domain.exception;

import com.proximity.vending.domain.exception.commons.BaseException;
import com.proximity.vending.domain.exception.commons.ExceptionCodeConstants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class NotFoundEntityException extends BaseException {

    private final Class<?> clazz;
    private final String message;

    public NotFoundEntityException(Class<?> clazz) {
        super(ExceptionCodeConstants.NOT_FOUND_ENTITY_EXCEPTION);
        this.clazz = clazz;
        this.message = StringUtils.EMPTY;
    }

    public NotFoundEntityException(Class<?> clazz, String message) {
        super(ExceptionCodeConstants.NOT_FOUND_ENTITY_EXCEPTION);
        this.clazz = clazz;
        this.message = message;
    }
}

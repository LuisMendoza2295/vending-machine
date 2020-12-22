package com.proximity.vending.domain.exception.commons;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionResponse {

    private String message;
    private String errorCode;

    public static ExceptionResponse of(String message, ExceptionCode exceptionCode){
        return new ExceptionResponse(message, exceptionCode.getCode());
    }
}

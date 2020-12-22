package com.proximity.vending.domain.exception.commons;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseException extends RuntimeException {

    private final String errorCode;
}

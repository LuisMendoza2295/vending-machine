package com.proximity.vending.admin.exception;

import com.proximity.vending.domain.exception.*;
import com.proximity.vending.domain.exception.commons.ExceptionCode;
import com.proximity.vending.domain.exception.commons.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_CODE_HEADER_NAME = "errorCode";

    @ExceptionHandler(BlockedMachineException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleBlockedMachineException(BlockedMachineException e) {
        log.error("MACHINE {} IS BLOCKED", e.getVendingMachineID());
        return ExceptionResponse.of(e.getMessage(), ExceptionCode.BLOCKED_MACHINE);
    }

    @ExceptionHandler(CardPaymentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleCardPaymentException(CardPaymentException e) {
        log.error("CARD {} COULD NOT BE PROCESSED", e.getIssuer());
        return ExceptionResponse.of(e.getMessage(), ExceptionCode.CARD_PAYMENT);
    }

    @ExceptionHandler(CashChangeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handlerCashChangeException(CashChangeException e) {
        log.error("MACHINE {} COULD NOT PROCESS CHANGE FOR {}", e.getVendingMachineID(), e.getChangeAmount());
        return ExceptionResponse.of(e.getMessage(), ExceptionCode.CASH_CHANGE);
    }

    @ExceptionHandler(InvalidDataException.class)
    // @ResponseBody
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionResponse> handleInvalidDataException(InvalidDataException e) {
        log.error("INVALID DATA {}::{}", e.getClazz(), e.getValue());
        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), ExceptionCode.INVALID_DATA);
        return new ResponseEntity<>(response, getHeaders(e.getErrorCode()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundEntityException(NotFoundEntityException e) {
        log.error("ENTITY {} NOT FOUND: {}", e.getClazz().getName(), e.getMessage());
        return ExceptionResponse.of(e.getMessage(), ExceptionCode.NOT_FOUND_ENTITY);
    }

    @ExceptionHandler(OutOfStockException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleOutOfStockException(OutOfStockException e) {
        log.error("MACHINE {} DOES NOT HAVE ENOUGH STOCK OF PRODUCT {}", e.getVendingMachineID(), e.getProductID());
        return ExceptionResponse.of(e.getMessage(), ExceptionCode.OUT_OF_STOCK);
    }

    @ExceptionHandler(OpenMachineException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleOpenMachineException(OpenMachineException e) {
        log.error("MACHINE {} IS OPEN AND CANNOT HANDLE ANY OPERATION", e.getVendingMachineID());
        return ExceptionResponse.of(e.getMessage(), ExceptionCode.OPEN_MACHINE);
    }

    private MultiValueMap<String, String> getHeaders(String errorCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ERROR_CODE_HEADER_NAME, errorCode);
        return headers;
    }
}

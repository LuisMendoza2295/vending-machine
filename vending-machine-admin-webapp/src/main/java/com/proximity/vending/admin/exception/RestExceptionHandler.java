package com.proximity.vending.admin.exception;

import com.proximity.vending.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BlockedMachineException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleBlockedMachineException(BlockedMachineException e) {
        log.error("MACHINE {} IS BLOCKED", e.getVendingMachineID());
        return e.getMessage();
    }

    @ExceptionHandler(CardPaymentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleCardPaymentException(CardPaymentException e) {
        log.error("CARD {} COULD NOT BE PROCESSED", e.getIssuer());
        return e.getMessage();
    }

    @ExceptionHandler(CashChangeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handlerCashChangeException(CashChangeException e) {
        log.error("MACHINE {} COULD NOT PROCESS CHANGE FOR {}", e.getVendingMachineID(), e.getChangeAmount());
        return e.getMessage();
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInvalidDataException(InvalidDataException e) {
        log.error("INVALID DATA {}::{}", e.getClazz(), e.getValue());
        return e.getMessage();
    }

    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundEntityException(NotFoundEntityException e) {
        log.error("ENTITY {} NOT FOUND: {}", e.getClazz().getName(), e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(OutOfStockException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleOutOfStockException(OutOfStockException e) {
        log.error("MACHINE {} DOES NOT HAVE ENOUGH STOCK OF PRODUCT {}", e.getVendingMachineID(), e.getProductID());
        return e.getMessage();
    }
}

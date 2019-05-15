package com.vvorobel.bonds4all.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super("bond can't be sold due to regulations");
    }
}

@ControllerAdvice
class ValidationOfRegulationsAdvice {

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String ValidationExceptionHandler(ValidationException ex) {
        return ex.getMessage();
    }
}
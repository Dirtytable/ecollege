package com.example.ecollege.api.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

//@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler {
    private final MessageSource messageSource;
    private static final String UNEXPECTED_ERROR = "exception.unexpected";
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
    @Autowired
    public RestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestMessage> handleArgumentNotValidException(MethodArgumentNotValidException ex, Locale locale) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errorMessages = new HashMap<>();
        for (int i = 0; i < result.getErrorCount(); i++) {
            String fieldName = result.getFieldErrors().get(i).getField();
            String message = messageSource.getMessage(result.getAllErrors().get(i), locale);
            errorMessages.put( fieldName, message);
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        logger.error("Invalid Input Exception: {}", errorMessages);
        return new ResponseEntity<>(new RestMessage(status, "MethodArgumentNotValidException",
                errorMessages), status);
    }
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<RestMessage> handleInvalidRoleException(InvalidRoleException ex, Locale locale){
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("roles", messageSource.getMessage(ex.getMessage(), null, locale));
        HttpStatus status = HttpStatus.NOT_FOUND;
        logger.error("InvalidRole Exception: {}", errorMessages);
        return new ResponseEntity<>(new RestMessage(status, "InvalidRole Exception", errorMessages), status);
    }
    @ExceptionHandler(CustomInvalidParameterException.class)
    public ResponseEntity<RestMessage> handleArgumentExistException(CustomInvalidParameterException ex, Locale locale){
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put(ex.getFiledName(), messageSource.getMessage(ex.getMessage(), null, locale));
        HttpStatus status = HttpStatus.NOT_FOUND;
        logger.error("InvalidParameter Exception: {}", errorMessages);
        return new ResponseEntity<>(new RestMessage(status, "InvalidParameter Exception", errorMessages), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestMessage> handleExceptions(Exception ex, Locale locale) {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put(null, messageSource.getMessage(UNEXPECTED_ERROR, null, locale));
        logger.error("Unexpected Exception: {}", errorMessages);
        return new ResponseEntity<>(new RestMessage(HttpStatus.INTERNAL_SERVER_ERROR,"Exception", errorMessages), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
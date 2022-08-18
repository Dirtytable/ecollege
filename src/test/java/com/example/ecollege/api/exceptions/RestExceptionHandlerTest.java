package com.example.ecollege.api.exceptions;

import com.example.ecollege.api.controllers.AuthController;
import com.example.ecollege.application.playoad.request.LoginRequest;
import com.example.ecollege.application.playoad.request.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.internal.asm.commons.Method;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {

    @Mock
    MessageSource messageSource;

    RestExceptionHandler restExceptionHandler;

    @BeforeEach
    void setUp(){
        this.restExceptionHandler = new RestExceptionHandler(this.messageSource);
    }

    @Test
    void handleArgumentNotValidException() {
        Locale locale = new Locale("en");
        LoginRequest loginRequest = new LoginRequest(" ", "password");
        BindingResult bindingResult = new BindException(loginRequest, "empty");
        bindingResult.addError(new FieldError("user", "username", "empty"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        given(messageSource.getMessage(bindingResult.getAllErrors().get(0), locale)).willReturn("empty");
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.minusNanos(localDateTime.getNano()).minusSeconds(localDateTime.getSecond());

        ResponseEntity<RestMessage> result = restExceptionHandler.handleArgumentNotValidException(exception, locale);
        assertThat(Objects.requireNonNull(result.getBody()).getMessage()).isEqualTo("MethodArgumentNotValidException");
        assertThat(result.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().getMessages()).containsEntry("username", "empty");
        assertThat(result.getBody().getTimestamp()).isEqualTo(localDateTime);

    }

    @Test
    void handleInvalidRoleException() {

        given(messageSource.getMessage("roles.notBlank", null, new Locale("en"))).willReturn("roles should be blank");
        ResponseEntity<RestMessage> result = restExceptionHandler.handleInvalidRoleException(new InvalidRoleException(), new Locale("en"));
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().getMessage()).isEqualTo("InvalidRole Exception");
        assertThat(result.getBody().getMessages()).containsEntry("roles","roles should be blank");


        verify(messageSource).getMessage("roles.notBlank", null, new Locale("en"));

    }

    @Test
    void handleArgumentExistException() {

        given(messageSource.getMessage("user.id.notFound", null, new Locale("en")))
                .willReturn("user not found");

        ResponseEntity<RestMessage> result = restExceptionHandler.handleArgumentExistException(
                new CustomInvalidParameterException("user.id.notFound", "user"), new Locale("en"));
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().getMessage()).isEqualTo("InvalidParameter Exception");
        assertThat(result.getBody().getMessages()).containsEntry("user","user not found");


        verify(messageSource).getMessage("user.id.notFound", null, new Locale("en"));
    }

    @Test
    void handleExceptions() {
        given(messageSource.getMessage("exception.unexpected", null, new Locale("en")))
                .willReturn("Unexpected error");

        ResponseEntity<RestMessage> result = restExceptionHandler.handleExceptions(new Exception(), new Locale("en"));
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody().getMessage()).isEqualTo("Exception");
        assertThat(result.getBody().getMessages()).containsEntry(null,"Unexpected error");

        verify(messageSource).getMessage("exception.unexpected", null, new Locale("en"));
    }
}
package com.example.ecollege.api.controllers;

import com.example.ecollege.application.playoad.request.LoginRequest;
import com.example.ecollege.application.playoad.request.UserRegisterRequest;
import com.example.ecollege.application.playoad.responce.JwtResponse;
import com.example.ecollege.application.service.JwtService;
import com.example.ecollege.application.service.UserService;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    UserService userService;
    @Mock
    JwtService jwtService;

    AuthController authController;

    @BeforeEach
    void setUp(){
        this.authController = new AuthController(this.userService, this.jwtService);
    }

    @Test
    void authenticateUser() {

        Set<String> strRoles = new HashSet<>();
        strRoles.add("admin");
        strRoles.add("user");
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("username", "password",
                "email@gmail.com", "realname", "group", strRoles);
        LoginRequest loginRequest = new LoginRequest(userRegisterRequest.getUsername(), userRegisterRequest.getPassword());
        Locale locale = new Locale("en");

        ResponseEntity<JwtResponse> result = authController.registerUser(locale, new UserRegisterRequest("username", "password",
                        "email@gmail.com", "realname", "group", strRoles));
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<UserRegisterRequest> userRegisterRequestArgumentCaptor = ArgumentCaptor.forClass(UserRegisterRequest.class);
        verify(userService).createUser(userRegisterRequestArgumentCaptor.capture());
        UserRegisterRequest captureUserRegisterRequest = userRegisterRequestArgumentCaptor.getValue();
        assertThat(captureUserRegisterRequest.toString()).hasToString(userRegisterRequest.toString());

        ArgumentCaptor<LoginRequest> loginRequestArgumentCaptor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(jwtService).createJwtResponse(loginRequestArgumentCaptor.capture());
        LoginRequest captureLoginRequest = loginRequestArgumentCaptor.getValue();
        assertThat(captureLoginRequest.toString()).hasToString(loginRequest.toString());

    }

    @Test
    void registerUser() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Locale locale = new Locale("en");

        ResponseEntity<JwtResponse> result =  authController.authenticateUser(locale, new LoginRequest("username", "password"));
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<LoginRequest> loginRequestArgumentCaptor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(jwtService).createJwtResponse(loginRequestArgumentCaptor.capture());
        LoginRequest captureLoginRequest = loginRequestArgumentCaptor.getValue();
        assertThat(captureLoginRequest.toString()).hasToString(loginRequest.toString());
    }
}
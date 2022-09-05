package com.example.ecollege.api.controllers;

import com.example.ecollege.api.core.repository.UserRepository;

import com.example.ecollege.application.playoad.request.LoginRequest;
import com.example.ecollege.application.playoad.request.UserRegisterRequest;
import com.example.ecollege.application.playoad.responce.JwtResponse;
import com.example.ecollege.application.service.JwtService;
import com.example.ecollege.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    UserService userService;
    JwtService jwtService;

    @Autowired
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;

    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestHeader(name = "Accept-Language", required = false) final Locale locale, @Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(jwtService.createJwtResponse(loginRequest), HttpStatus.OK);
    }


    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> registerUser(@RequestHeader(name = "Accept-Language", required = false) final Locale locale, @Valid @RequestBody UserRegisterRequest userRegisterRequest) {

        userService.createUser(userRegisterRequest);
        LoginRequest loginRequest = new LoginRequest(userRegisterRequest.getUsername(), userRegisterRequest.getPassword());
        return new ResponseEntity<>(jwtService.createJwtResponse(loginRequest), HttpStatus.OK);
    }


}

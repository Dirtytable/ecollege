package com.example.ecollege.application.playoad.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@ToString
public class LoginRequest {
    @NotBlank(message = "{username.notBlank}")
    private String username;

    @NotBlank(message = "{password.notBlank}")
    private String password;


}
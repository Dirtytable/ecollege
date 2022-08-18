package com.example.ecollege.application.playoad.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
@Getter
@AllArgsConstructor
@ToString
public class UserUpdateRequest {
    @NotBlank(message = "{password.notBlank}")
    private String password;
    @NotBlank(message = "{realName.notBlank}")
    private String realName;
    @NotBlank(message = "{group.notBlank}")
    private String group;
}

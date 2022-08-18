package com.example.ecollege.application.playoad.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegisterRequest {
    @NotBlank(message = "{username.notBlank}")
    private String username;
    @NotBlank(message = "{password.notBlank}")
    private String password;
    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.isEmail}")
    private String email;
    @NotBlank(message = "{realName.notBlank}")
    private String realName;
    @NotBlank(message = "{group.notBlank}")
    private String group;
    private Set<String> roles;

}
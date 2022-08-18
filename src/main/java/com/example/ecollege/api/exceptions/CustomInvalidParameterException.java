package com.example.ecollege.api.exceptions;

import lombok.Getter;


@Getter
public class CustomInvalidParameterException extends IllegalArgumentException {
    private final String filedName;
    public CustomInvalidParameterException(String error, String filedName) {
        super(error);
        this.filedName = filedName;
    }
}

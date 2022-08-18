package com.example.ecollege.api.exceptions;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("roles.notBlank");
    }
}

package com.example.ecollege.api.security.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {
    AuthEntryPointJwt authEntryPointJwt = new AuthEntryPointJwt();

    @Test
    void commence() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response= new MockHttpServletResponse();
        AuthenticationException authenticationException = new AccountExpiredException("", new IOException());
        authEntryPointJwt.commence(request, response, authenticationException);
        assertThat(response.getErrorMessage()).isEqualTo("Error: Unauthorized");
    }
}
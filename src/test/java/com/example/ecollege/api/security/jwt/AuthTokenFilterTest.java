package com.example.ecollege.api.security.jwt;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import com.example.ecollege.api.security.services.UserDetailsImpl;
import com.example.ecollege.api.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    AuthTokenFilter authTokenFilter;

    @BeforeEach
    void setUp(){
        authTokenFilter = new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Test
    void canDoFilterInternal() throws ServletException, IOException {
        String username = "username";

        MockHttpServletResponse response = new MockHttpServletResponse();

        MockHttpServletRequest request = new MockHttpServletRequest();
        String jwt = Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 1000000))
                .signWith(SignatureAlgorithm.HS256,
                        "sdsadnasjknkjakjbddjasndlsnkndsalkdlas312t38sadsa"
                )
                .compact();
        String headerAuth = "Bearer "+jwt;
        request.addHeader("Authorization", headerAuth);

        given(jwtUtils.validateJwtToken(jwt)).willReturn(true);
        given(jwtUtils.getUserNameFromJwtToken(jwt)).willReturn(username);

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_ADMIN));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                "id", username,
                "password", "email@mail.com",
                "realName", "group", roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList()));
        given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);

        MockFilterChain mockFilterChain = new MockFilterChain();

        authTokenFilter.doFilterInternal(request, response, mockFilterChain);

        verify(jwtUtils).validateJwtToken(jwt);
        verify(jwtUtils).getUserNameFromJwtToken(jwt);
        verify(userDetailsService).loadUserByUsername(username);

    }


    @Test
    void willThrowException() throws ServletException, IOException {
        String username = "username";

        MockHttpServletResponse response = new MockHttpServletResponse();

        MockHttpServletRequest request = new MockHttpServletRequest();
        String jwt = Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 1000000))
                .signWith(SignatureAlgorithm.HS256,
                        "sdsadnasjknkjakjbddjasndlsnkndsalkdlas312t38sadsa"
                )
                .compact();
        String headerAuth = "Bearer "+jwt;
        request.addHeader("Authorization", headerAuth);

        given(jwtUtils.validateJwtToken(jwt)).willReturn(true);
        given(jwtUtils.getUserNameFromJwtToken(jwt)).willReturn(username);

        MockFilterChain mockFilterChain = new MockFilterChain();

        authTokenFilter.doFilterInternal(request, response, mockFilterChain);
    }
}
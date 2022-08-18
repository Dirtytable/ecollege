package com.example.ecollege.application.service;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import com.example.ecollege.api.core.model.User;
import com.example.ecollege.api.core.repository.RoleRepository;
import com.example.ecollege.api.core.repository.UserRepository;
import com.example.ecollege.api.exceptions.CustomInvalidParameterException;
import com.example.ecollege.api.security.jwt.JwtUtils;
import com.example.ecollege.api.security.services.UserDetailsImpl;
import com.example.ecollege.application.playoad.request.LoginRequest;
import com.example.ecollege.application.playoad.responce.JwtResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {


    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtils jwtUtils;
    JwtService jwtService;



    @BeforeEach
    void setUp(){
        jwtService = new JwtService(this.authenticationManager, this.jwtUtils);

    }

    @Test
    void canCreateJwtResponse() {
        String jwtToken = "token";
        LoginRequest loginRequest = new LoginRequest("username", "password");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_ADMIN));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new UserDetailsImpl(
                        "id", loginRequest.getUsername(),
                        loginRequest.getPassword(), "email@mail.com",
                        "realName", "group", roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                        .collect(Collectors.toList())),
                loginRequest.getPassword()
                );

        given(authenticationManager.authenticate(token)).willReturn(authentication);

        given(jwtUtils.generateJwtToken(authentication)).willReturn(jwtToken);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                "id", loginRequest.getUsername(),
                loginRequest.getPassword(), "email@mail.com",
                "realName", "group", roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList()));

        JwtResponse result = jwtService.createJwtResponse(loginRequest);

        assertThat(result.getToken()).isEqualTo(jwtToken);
        assertThat(result.getId()).isEqualTo(userDetails.getId());
        assertThat(result.getEmail()).isEqualTo(userDetails.getEmail());
        assertThat(result.getGroup()).isEqualTo(userDetails.getGroup());
        assertThat(result.getRoles()).isEqualTo(userDetails.
                getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        assertThat(result.getRealName()).isEqualTo(userDetails.getRealName());
        assertThat(result.getUsername()).isEqualTo(userDetails.getUsername());

        verify(authenticationManager).authenticate(token);

        verify(jwtUtils).generateJwtToken(authentication);

    }
}
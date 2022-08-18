package com.example.ecollege.api.security;

import com.example.ecollege.api.core.model.User;
import com.example.ecollege.api.security.jwt.AuthEntryPointJwt;
import com.example.ecollege.api.security.jwt.AuthTokenFilter;
import com.example.ecollege.api.security.jwt.JwtUtils;
import com.example.ecollege.api.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataMongoTest
@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @Mock
    UserDetailsServiceImpl userDetailsService;
    @Mock
    AuthEntryPointJwt authEntryPointJwt;
    @Mock
    JwtUtils jwtUtils;

    WebSecurityConfig webSecurityConfig;

    @Mock
    ObjectPostProcessor<Object> objectPostProcessor;



    @BeforeEach
    void setUp(){
        this.webSecurityConfig =
                new WebSecurityConfig(this.userDetailsService, this.authEntryPointJwt, this.jwtUtils);
    }
    @Test
    void authenticationJwtTokenFilter() {
        AuthTokenFilter result = webSecurityConfig.authenticationJwtTokenFilter();
        assertThat(result).isNotNull();
    }

    @Test
    void configure() throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor);
        webSecurityConfig.configure(authenticationManagerBuilder);
        assertThat(authenticationManagerBuilder.userDetailsService(userDetailsService)).isNotNull();
    }




}
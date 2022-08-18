package com.example.ecollege.api.security.services;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import com.example.ecollege.api.core.model.User;
import com.example.ecollege.api.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;
    UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp(){
        this.userDetailsService = new UserDetailsServiceImpl(this.userRepository);
    }

    @Test
    void loadUserByUsername() {
        String username = "username";
        User user = new User(username, "password",
                "email@gamil.com",
                "realName", "group");
        user.setId("id");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_ADMIN));
        user.setRoles(roles);
        given(userRepository.findByUsername(username))
                .willReturn(user);

        UserDetailsImpl result = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getGroup()).isEqualTo(user.getGroup());
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
        assertThat(result.getRealName()).isEqualTo(user.getRealName());
        assertThat(result.getAuthorities()).isEqualTo(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList()));

        verify(userRepository).findByUsername(username);
    }
}
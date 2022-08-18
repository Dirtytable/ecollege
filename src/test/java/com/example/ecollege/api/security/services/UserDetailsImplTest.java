package com.example.ecollege.api.security.services;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import com.example.ecollege.api.core.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    UserDetailsImpl userDetails = new UserDetailsImpl("id", null, null, null, null,null,null);


    @Test
    void canBuild() {
        User user = new User("username", "password",
                "email@gamil.com",
                "realName", "group");
        user.setId("id");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_ADMIN));
        user.setRoles(roles);

        UserDetailsImpl result = UserDetailsImpl.build(user);
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getGroup()).isEqualTo(user.getGroup());
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
        assertThat(result.getRealName()).isEqualTo(user.getRealName());
        assertThat(result.getAuthorities()).isEqualTo(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList()));
    }


    @Test
    void isAccountNonExpired() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked() {
        assertThat(userDetails.isAccountNonLocked()).isTrue();

    }

    @Test
    void isCredentialsNonExpired() {
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();

    }

    @Test
    void isEnabled() {
        assertThat(userDetails.isEnabled()).isTrue();

    }

    @Test
    void sameClassTestEqualsTrue() {
        assertThat(userDetails.equals(userDetails)).isTrue();

    }

    @Test
    void nullClassTestEqualsFalse() {
        assertThat(userDetails.equals(null)).isFalse();
        assertThat(userDetails.equals(new UserDetailsImpl())).isFalse();
    }

    @Test
    void newClassTestEqualsFalse() {
        assertThat(userDetails.equals(new UserDetailsImpl())).isFalse();
    }
    @Test
    void testEqualsTrue() {
        UserDetails details = new UserDetailsImpl("id", null,null,null,null,null,null);
        assertThat(userDetails.equals(details)).isTrue();

    }

}
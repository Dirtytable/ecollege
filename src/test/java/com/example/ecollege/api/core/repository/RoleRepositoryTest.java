package com.example.ecollege.api.core.repository;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class RoleRepositoryTest {

    RoleRepository roleRepository;

    @Autowired
    public RoleRepositoryTest(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Test
    void canFindByName() {
        Role roleAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));
        Role roleUser = roleRepository.save(new Role(ERole.ROLE_USER));

        Role result = roleRepository.findByName(ERole.ROLE_USER);
        assertThat(result.getName()).isEqualTo(ERole.ROLE_USER);
        assertThat(result.getId()).isEqualTo(roleUser.getId());
        result = roleRepository.findByName(ERole.ROLE_ADMIN);
        assertThat(result.getName()).isEqualTo(ERole.ROLE_ADMIN);
        assertThat(result.getId()).isEqualTo(roleAdmin.getId());

    }
}
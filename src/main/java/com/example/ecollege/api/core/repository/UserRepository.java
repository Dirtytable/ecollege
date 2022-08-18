package com.example.ecollege.api.core.repository;

import com.example.ecollege.api.core.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    List<User> findByGroup(String group);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

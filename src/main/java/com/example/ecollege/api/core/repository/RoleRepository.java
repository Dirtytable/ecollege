package com.example.ecollege.api.core.repository;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(ERole name);

}

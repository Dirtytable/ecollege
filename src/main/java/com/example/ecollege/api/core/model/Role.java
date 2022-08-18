package com.example.ecollege.api.core.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter

@Document(collection = "roles")
public class Role {

    @Id
    private  String id;
    private ERole name;

    public Role(ERole name) {
        this.name = name;
    }

    public Role() {
    }


}

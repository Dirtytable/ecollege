package com.example.ecollege.api.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document(collection ="users")
public class User {
    @Id
    private String id;
    @DBRef
    private Set<Role> roles ;
    private String username;
    private String password;
    private String email;
    private String realName;
    private String group;

    private List<String> lessonsId ;

    public User() {
    }

    public User(String username, String password, String email, String realName, String group) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.realName = realName;
        this.group = group;
        this.lessonsId = new ArrayList<>();
        this.roles = new HashSet<>();
    }


}

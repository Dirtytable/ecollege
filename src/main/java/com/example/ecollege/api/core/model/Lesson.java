package com.example.ecollege.api.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "lessons")
public class Lesson {
    @Id
    private String id;
    private String name;
    private String description;
    private List<String> usersId;
    public Lesson() {
    }
    public Lesson(String name, String description) {
        this.name = name;
        this.description = description;
        this.usersId = new ArrayList<>();
    }


}

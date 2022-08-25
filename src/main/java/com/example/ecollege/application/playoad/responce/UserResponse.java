package com.example.ecollege.application.playoad.responce;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter

@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String group;
    private List<String> lessonsId;
}

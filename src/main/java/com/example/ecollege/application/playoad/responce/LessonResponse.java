package com.example.ecollege.application.playoad.responce;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class LessonResponse {
    private String id;
    private String name;
    private String description;
    private List<String> usersId;

}

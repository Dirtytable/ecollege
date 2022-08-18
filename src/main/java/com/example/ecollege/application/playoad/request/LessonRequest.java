package com.example.ecollege.application.playoad.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class LessonRequest {
    @NotBlank(message = "{name.notBlank}")
    private String name;
    @NotBlank(message = "{description.notBlank}")
    private String description;
}

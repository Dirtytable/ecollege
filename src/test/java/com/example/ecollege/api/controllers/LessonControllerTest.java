package com.example.ecollege.api.controllers;

import com.example.ecollege.api.core.model.Lesson;
import com.example.ecollege.application.playoad.request.LessonRequest;
import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import com.example.ecollege.application.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class LessonControllerTest {

    @Mock
    LessonService lessonService;

    LessonController lessonController;

    @BeforeEach
    void setUp(){
        this.lessonController = new LessonController(this.lessonService);
    }

    @Test
    void postLesson() {
        String name= "name";
        String description = "des";
        
        ResponseEntity<LessonResponse> result = lessonController.postLesson(new Locale("en"), new LessonRequest(name, description));
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        
        LessonRequest expected = new LessonRequest(name, description);
        ArgumentCaptor<LessonRequest> argumentCaptor = ArgumentCaptor.forClass(LessonRequest.class);
        verify(lessonService).createLesson(argumentCaptor.capture());
        LessonRequest captureRequest = argumentCaptor.getValue();
        assertThat(captureRequest.toString()).hasToString(expected.toString());

    }

    @Test
    void getAllLessons() {

        ResponseEntity<List<LessonResponse>> result =  lessonController.getAllLessons(new Locale("en"));
        assertThat(result.getBody()).isEmpty();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        verify(lessonService).getAllLessons();
    }

    @Test
    void getLessonById() {
        String id = "id";

        ResponseEntity<LessonResponse> result = lessonController.getLessonById(new Locale("en"),id);
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        verify(lessonService).getLessonById(id);
    }

    @Test
    void updateLesson() {
        String id = "id";
        String name = "name";
        String des = "des";

        ResponseEntity<LessonResponse> result = lessonController.updateLesson(new Locale("en"), 
                id, new LessonRequest(name, des));
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        LessonRequest lessonRequest = new LessonRequest(name, des);
        ArgumentCaptor<LessonRequest> argumentCaptor = ArgumentCaptor.forClass(LessonRequest.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(lessonService).updateLesson(stringArgumentCaptor.capture(), argumentCaptor.capture());
        LessonRequest captureLessonRequest = argumentCaptor.getValue();
        String captureId = stringArgumentCaptor.getValue();
        assertThat(captureId).isEqualTo(id);
        assertThat(captureLessonRequest.toString()).hasToString(lessonRequest.toString());
    }

    @Test
    void getAllUserFromLesson() {
        String lessonId = "id";

        ResponseEntity<List<UserResponse>> result = lessonController.getAllUserFromLesson(new Locale("en"), lessonId);
        assertThat(result.getBody()).isEmpty();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        verify(lessonService).getAllUsersFromLesson(lessonId);

    }

    @Test
    void deleteLesson() {
        String lessonId = "id";
        given(lessonService.deleteLesson(lessonId)).willReturn(HttpStatus.NO_CONTENT);

        ResponseEntity<HttpStatus> result = lessonController.deleteLesson(new Locale("en"), lessonId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(lessonService).deleteLesson(lessonId);

    }

    @Test
    void putUserToLessonByName() {
        String lessonId = "id";
        String username = "username";

        ResponseEntity<LessonResponse> result = lessonController.putUserToLessonByName(new Locale("en"), lessonId, username);
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(lessonService).putUserToLessonByName(lessonId, username);

    }

    @Test
    void deleteUserFromLesson() {
        String lessonId = "id";
        String username = "username";
        ResponseEntity<LessonResponse> result = lessonController.deleteUserFromLesson(new Locale("en"), lessonId, username);
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(lessonService).deleteUserFromLesson(lessonId, username);
    }
}
package com.example.ecollege.api.controllers;

import com.example.ecollege.application.playoad.request.UserUpdateRequest;
import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import com.example.ecollege.application.service.LessonService;
import com.example.ecollege.application.service.UserService;
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
class UserControllerTest {

    UserController userController;

    @Mock
    UserService userService;

    @BeforeEach
    void setUp(){
        this.userController = new UserController(this.userService);
    }
    @Test
    void getAllUsers() {

        ResponseEntity<List<UserResponse>> result = userController.getAllUsers(new Locale("en"));
        assertThat(result.getBody()).isEmpty();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).getAllUsers();

    }

    @Test
    void getAllUsersByGroup() {
        String group = "group";
        ResponseEntity<List<UserResponse>> result = userController.getAllUsersByGroup(new Locale("en"), group);
        assertThat(result.getBody()).isEmpty();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(userService).getAllUsersByGroup(group);

    }

    @Test
    void getUserById() {
        String userId = "id";
        ResponseEntity<UserResponse> result = userController.getUserById(new Locale("en"), userId);
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(userService).getUserById(userId);
    }

    @Test
    void updateUser() {
        String userId = "id";

        ResponseEntity<UserResponse> result = userController.updateUser(new Locale("en"), userId,new UserUpdateRequest("password", "name", "group"));
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<UserUpdateRequest> argumentCaptor = ArgumentCaptor.forClass(UserUpdateRequest.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService).updateUser(stringArgumentCaptor.capture(), argumentCaptor.capture());
        UserUpdateRequest captureRequest = argumentCaptor.getValue();
        String captureId = stringArgumentCaptor.getValue();
        assertThat(captureId).isEqualTo(userId);
        UserUpdateRequest request = new UserUpdateRequest("password", "name", "group");
        assertThat(captureRequest.toString()).hasToString(request.toString());
    }

    @Test
    void deleteUser() {
        String userId = "id";
        given(userService.deleteUser(userId)).willReturn(HttpStatus.NO_CONTENT);
        ResponseEntity<HttpStatus> result = userController.deleteUser(new Locale("en"), userId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(userService).deleteUser(userId);
    }

    @Test
    void getAllLessonFromUser() {
        String userId = "id";
        ResponseEntity<List<LessonResponse>> result = userController.getAllLessonFromUser(new Locale("en"), userId);
        assertThat(result.getBody()).isEmpty();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        verify(userService).getAllLessonFromUser(userId);
    }
}
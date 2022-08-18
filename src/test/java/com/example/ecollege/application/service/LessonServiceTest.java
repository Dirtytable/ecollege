package com.example.ecollege.application.service;

import com.example.ecollege.api.core.model.Lesson;
import com.example.ecollege.api.core.model.User;
import com.example.ecollege.api.core.repository.LessonRepository;
import com.example.ecollege.api.core.repository.UserRepository;
import com.example.ecollege.api.exceptions.CustomInvalidParameterException;
import com.example.ecollege.application.playoad.request.LessonRequest;
import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import lombok.experimental.ExtensionMethod;
import org.apache.catalina.authenticator.SavedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class LessonServiceTest {


    @Mock
    LessonRepository lessonRepository;
    @Mock
    UserRepository userRepository;

    @MockBean
    ModelMapper modelMapper;
    LessonService lessonService;

    @BeforeEach
    void setUp(){
        this.lessonService = new LessonService(this.lessonRepository, this.userRepository);
        this.lessonService.setModelMapper(modelMapper);
    }

    private void assertLessonResponseResult(LessonResponse result, LessonResponse lessonResponseExpected){
        assertThat(result.getId()).isEqualTo(lessonResponseExpected.getId());
        assertThat(result.getName()).isEqualTo(lessonResponseExpected.getName());
        assertThat(result.getDescription()).isEqualTo(lessonResponseExpected.getDescription());
        assertThat(result.getUsersId()).isEqualTo(lessonResponseExpected.getUsersId());
        for (int i = 0; i < result.getUsersId().size(); i++) {
            assertThat(result.getUsersId().get(i))
                    .isEqualTo(lessonResponseExpected.getUsersId().get(i));
        }
    }
    
    private LessonResponse setUpLessonResponse(Lesson lesson, String lessonId, 
                                             String name, String description, List<String> usersId){

        given(modelMapper.map(lesson, LessonResponse.class))
                .willReturn(new LessonResponse( lessonId, name,description, usersId));
        return new LessonResponse( lessonId, name,description, usersId);
    }

    //createLesson
    @Test
    void canCreateLesson() {
        LessonRequest lessonRequest = new LessonRequest("name", "des");
        String lessonId = "id";

        LessonResponse lessonResponseExpected = setUpLessonResponse(null, lessonId, 
                lessonRequest.getName(), lessonRequest.getDescription(), new ArrayList<>());

        LessonResponse result = lessonService.createLesson(lessonRequest);

        assertLessonResponseResult(result, lessonResponseExpected);

        ArgumentCaptor<Lesson> lessonArgumentCaptor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository).save(lessonArgumentCaptor.capture());
        Lesson captureLesson = lessonArgumentCaptor.getValue();
        assertThat(captureLesson.getName()).isEqualTo(lessonRequest.getName());
        assertThat(captureLesson.getDescription()).isEqualTo(lessonRequest.getDescription());

        verify(modelMapper).map(null, LessonResponse.class);
    }
//    getAllLessons
    @Test
    void canGetAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        List<LessonResponse> lessonResponses = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Lesson lesson = new Lesson();
            lessons.add(lesson);
            lessonResponses.add(setUpLessonResponse(lesson, "id", "name",
                    "des", List.of(new String[]{"id"})));
        }

        given(lessonRepository.findAll()).willReturn(lessons);

        List<LessonResponse> result = lessonService.getAllLessons();
        for (int i = 0; i < 2; i++) {
            assertLessonResponseResult(result.get(i), lessonResponses.get(i));
        }

        verify(lessonRepository).findAll();
        for (Lesson lesson :lessons) {
            verify(modelMapper).map(lesson, LessonResponse.class);
        }

    }

    @Test
    void canGetLessonById() {
        String id = "id";
        Lesson lesson = new Lesson();
        lesson.setId(id);
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(id)).willReturn(lessonData);

        LessonResponse lessonResponse = setUpLessonResponse(lessonData.get(),
                id, "name", "des", List.of(new String[]{"id"})
        );

        LessonResponse result = lessonService.getLessonById(id);
        assertLessonResponseResult(result, lessonResponse);

        verify(lessonRepository).findById(id);
        verify(modelMapper).map(lessonData.get(), LessonResponse.class);
    }




    @Test
    void canUpdateLesson() {
        String id = "id";
        LessonRequest lessonRequest = new LessonRequest("name", "des");
        Optional<Lesson> oldLessonData = Optional.of(new Lesson());
        given(lessonRepository.findById(id)).willReturn(oldLessonData);

        LessonResponse lessonResponse = setUpLessonResponse(null, id,
                lessonRequest.getName(), lessonRequest.getDescription(), List.of(new String[]{"id"}));

        LessonResponse result = lessonService.updateLesson(id, lessonRequest);
        assertLessonResponseResult(result, lessonResponse);

        verify(lessonRepository).findById(id);
        ArgumentCaptor<Lesson> lessonArgumentCaptor = ArgumentCaptor.forClass(Lesson.class);

        verify(lessonRepository).save(lessonArgumentCaptor.capture());
        Lesson captureLesson = lessonArgumentCaptor.getValue();
        assertThat(captureLesson.getName()).isEqualTo(lessonRequest.getName());
        assertThat(captureLesson.getDescription()).isEqualTo(lessonRequest.getDescription());

        verify(modelMapper).map(null, LessonResponse.class);

    }

    @Test
    void canGetAllUsersFromLesson() {
        String lessonId = "id0";
        Lesson lesson = new Lesson();
        List<String> usersId = new ArrayList<>();
        List<UserResponse> userResponses = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for (int i = 1; i <3; i++) {
            String userId="id";
            usersId.add(userId+i);

            users.add(new User());
            Optional<User> userDAta = Optional.of(users.get(i-1));
            given(userRepository.findById(usersId.get(i-1))).willReturn(userDAta);

            userResponses.add(new UserResponse(usersId.get(i-1),
                    "username", "group", List.of(new String[]{lessonId})));
            given(modelMapper
                    .map(users.get(i-1), UserResponse.class))
                    .willReturn(new UserResponse(usersId.get(i-1),
                            "username", "group", List.of(new String[]{lessonId})));
        }
        lesson.setUsersId(usersId);
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(lessonId)).willReturn(lessonData);

        List<UserResponse> result = lessonService.getAllUsersFromLesson(lessonId);

        verify(lessonRepository).findById(lessonId);
        for (int i = 0; i < 2; i++) {
            assertThat(result.get(i).getId()).isEqualTo(userResponses.get(i).getId());
            assertThat(result.get(i).getUsername()).isEqualTo(userResponses.get(i).getUsername());
            assertThat(result.get(i).getGroup()).isEqualTo(userResponses.get(i).getGroup());
            assertThat(result.get(i).getLessonsId()).hasSize(userResponses.get(i).getLessonsId().size());
            assertThat(result.get(i).getLessonsId()).contains(lessonId);
            verify(userRepository).findById(lesson.getUsersId().get(i));
            verify(modelMapper).map(users.get(i), UserResponse.class);
        }
    }

    @Test
    void canDeleteLesson() {
        String lessonId = "id1";
        String userId = "id2";
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        List<String> usersId = new ArrayList<>();
        usersId.add(userId);
        lesson.setUsersId(usersId);
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(lessonId)).willReturn(lessonData);

        User user = new User();
        List<String> lessonsId = new ArrayList<>();
        lessonsId.add(lessonId);
        user.setLessonsId(lessonsId);
        Optional<User> userData = Optional.of(user);
        given(userRepository.findById(userId)).willReturn(userData);

        HttpStatus httpStatus = lessonService.deleteLesson(lessonId);
        assertThat(httpStatus).isEqualTo(HttpStatus.NO_CONTENT);

        verify(lessonRepository).findById(lessonId);
        verify(userRepository).findById(userId);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User captureUser = userArgumentCaptor.getValue();
        assertThat(captureUser.getLessonsId()).isEmpty();

        verify(lessonRepository).deleteById(lessonId);
    }

    @Test
    void canPutUserToLessonByName() {
        String lessonId = "id1";
        String userId = "id2";
        String username = "username";

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setUsersId(new ArrayList<>());
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(lessonId)).willReturn(lessonData);

        User user = new User();
        user.setId(userId);
        user.setLessonsId(new ArrayList<>());
        given(userRepository.findByUsername(username)).willReturn(user);

        LessonResponse lessonResponse = setUpLessonResponse(null,
                lessonId, "name", "des", List.of(new String[]{"id2"}));

        LessonResponse result = lessonService.putUserToLessonByName(lessonId,username);
        assertLessonResponseResult(result, lessonResponse);

        verify(lessonRepository).findById(lessonId);
        verify(userRepository).findByUsername(username);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        user = userArgumentCaptor.getValue();
        assertThat(user.getLessonsId()).hasSize(1);
        assertThat(user.getLessonsId().get(0)).isEqualTo(lessonId);

        ArgumentCaptor<Lesson> lessonArgumentCaptor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository).save(lessonArgumentCaptor.capture());
        lesson = lessonArgumentCaptor.getValue();
        assertThat(lesson.getUsersId()).hasSize(1);
        assertThat(lesson.getUsersId().get(0)).isEqualTo(userId);

        verify(modelMapper).map(null, LessonResponse.class);
    }

    @Test
    void canDeleteUserFromLesson() {
        String lessonId = "id1";
        String userId = "id2";
        String username = "username";

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        List<String> usersId = new ArrayList<>();
        usersId.add(userId);
        lesson.setUsersId(usersId);
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(lessonId)).willReturn(lessonData);

        User user = new User();
        user.setId(userId);
        List<String> lessonsId = new ArrayList<>();
        lessonsId.add(lessonId);
        user.setLessonsId(lessonsId);
        given(userRepository.findByUsername(username)).willReturn(user);
        given(userRepository.existsByUsername(username)).willReturn(true);

        LessonResponse lessonResponse = setUpLessonResponse(null,
                lessonId, "name", "des", new ArrayList<>()
        );

        LessonResponse result = lessonService.deleteUserFromLesson(lessonId, username);
        assertLessonResponseResult(result, lessonResponse);

        verify(lessonRepository).findById(lessonId);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).findByUsername(username);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User captureUser = userArgumentCaptor.getValue();
        assertThat(captureUser.getLessonsId()).isEmpty();

        ArgumentCaptor<Lesson> lessonArgumentCaptor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository).save(lessonArgumentCaptor.capture());
        Lesson captureLesson = lessonArgumentCaptor.getValue();
        assertThat(captureLesson.getUsersId()).isEmpty();

        verify(modelMapper).map(null, LessonResponse.class);
    }

    @Test
    void willThrowWhenLessonNotFound(){
        String id = "id";
        String username = "username";
        assertThatThrownBy(()-> lessonService.getLessonById(id))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.id.notFound", "id");
        LessonRequest lessonRequest = new LessonRequest();
        assertThatThrownBy(()-> lessonService.updateLesson(id, lessonRequest))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.id.notFound", "id");

        assertThatThrownBy(()-> lessonService.getAllUsersFromLesson(id))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.id.notFound", "id");

        assertThatThrownBy(()-> lessonService.deleteLesson(id))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.id.notFound", "id");

        assertThatThrownBy(()-> lessonService.putUserToLessonByName(id, username))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.id.notFound", "id");

        assertThatThrownBy(()-> lessonService.deleteUserFromLesson(id, username))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.id.notFound", "id");
    }
    @Test
    void willThrowWhenUsersIdIsEmptyInLesson(){
        String id = "id";
        Lesson lesson = new Lesson();
        lesson.setUsersId(new ArrayList<>());
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(id)).willReturn(lessonData);

        assertThatThrownBy(()->  lessonService.getAllUsersFromLesson(id))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.usersId.isEmpty", "usersId");

    }

    @Test
    void willThrowWhenUserAlreadyAddedInLesson(){
        String lessonId = "id1";
        String userId = "id2";
        String username = "username";

        Lesson lesson = new Lesson();
        List<String> usersId = new ArrayList<>();
        usersId.add(userId);
        lesson.setUsersId(usersId);
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(lessonId)).willReturn(lessonData);

        User user = new User();
        user.setId(userId);
        given(userRepository.findByUsername(username)).willReturn(user);

        assertThatThrownBy(()->  lessonService.putUserToLessonByName(lessonId, username))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.usersId.userAlreadyAdded", "username");

    }

    @Test
    void willThrowWhenUserNotFoundByUsername(){
        String lessonId = "id";
        String username = "username";
        Optional<Lesson> lessonData = Optional.of(new Lesson());
        given(lessonRepository.findById(lessonId)).willReturn(lessonData);

        assertThatThrownBy(()->  lessonService.deleteUserFromLesson(lessonId, username))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("user.username.notFound", "username");


    }

    @Test
    void willTrowWhenUserIdNotFoundInLesson(){
        String lessonId = "id1";
        String userId = "id2";
        String username = "username";
        Lesson lesson = new Lesson();

        lesson.setUsersId(new ArrayList<>());
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(lessonId)).willReturn(lessonData);
        given(userRepository.existsByUsername(username)).willReturn(true);

        User user = new User();
        user.setId(userId);
        given(userRepository.findByUsername(username)).willReturn(user);

        assertThatThrownBy(()->  lessonService.deleteUserFromLesson(lessonId, username))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("lesson.usersId.notFound", "userId");


    }


}
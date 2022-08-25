package com.example.ecollege.application.service;

import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Lesson;
import com.example.ecollege.api.core.model.Role;
import com.example.ecollege.api.core.model.User;
import com.example.ecollege.api.core.repository.LessonRepository;
import com.example.ecollege.api.core.repository.RoleRepository;
import com.example.ecollege.api.core.repository.UserRepository;
import com.example.ecollege.api.exceptions.CustomInvalidParameterException;
import com.example.ecollege.api.exceptions.InvalidRoleException;
import com.example.ecollege.application.playoad.request.UserRegisterRequest;
import com.example.ecollege.application.playoad.request.UserUpdateRequest;
import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private PasswordEncoder encoder;

    @MockBean
    private ModelMapper modelMapper;
    private UserService userService;


    @BeforeEach
    void setUp(){
        this.userService = new UserService(this.userRepository, this.roleRepository, this.lessonRepository, this.encoder);
        this.userService.setModelMapper(modelMapper);
    }


    private void assertUserResponseResult(UserResponse result, UserResponse lessonResponseExpected){
        assertThat(result.getId()).isEqualTo(lessonResponseExpected.getId());
        assertThat(result.getUsername()).isEqualTo(lessonResponseExpected.getUsername());
        assertThat(result.getGroup()).isEqualTo(lessonResponseExpected.getGroup());
        assertThat(result.getLessonsId()).isEqualTo(lessonResponseExpected.getLessonsId());
        for (int i = 0; i < result.getLessonsId().size(); i++) {
            assertThat(result.getLessonsId().get(i))
                    .isEqualTo(lessonResponseExpected.getLessonsId().get(i));
        }
    }

    private UserResponse setUpUserResponse(User user, String userId,
                                               String username, String group, List<String> lessonsId){

        given(modelMapper.map(user, UserResponse.class))
                .willReturn(new UserResponse(userId, username, group, lessonsId));
        return new UserResponse(userId, username, group, lessonsId);
    }

    @Test
    void canCreateUser() {
        //given
        Set<String> strRoles = new HashSet<>();
        strRoles.add("admin");
        strRoles.add("user");
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                "username",
                "password",
                "email@mail.com",
                "name",
                "group",
                strRoles
        );

        given(encoder.encode(userRegisterRequest.getPassword())).willReturn(userRegisterRequest.getPassword());

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(ERole.ROLE_ADMIN));
        roles.add(new Role(ERole.ROLE_USER));
        given(roleRepository.findByName(ERole.ROLE_ADMIN)).willReturn(roles.get(0));
        given(roleRepository.findByName(ERole.ROLE_USER)).willReturn(roles.get(1));

        UserResponse userResponse = setUpUserResponse(null, "id", userRegisterRequest.getUsername(),
                userRegisterRequest.getGroup(), new ArrayList<>());

        //when
        UserResponse result = userService.createUser(userRegisterRequest);

        assertUserResponseResult(result, userResponse);

        verify(userRepository).existsByUsername(userRegisterRequest.getUsername());
        verify(userRepository).existsByEmail(userRegisterRequest.getEmail());

        verify(roleRepository).findByName(ERole.ROLE_ADMIN);
        verify(roleRepository).findByName(ERole.ROLE_USER);
        verify(encoder).encode(userRegisterRequest.getPassword());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User captureUser = userArgumentCaptor.getValue();
        assertThat(captureUser.getUsername()).isEqualTo("username");
        assertThat(captureUser.getPassword()).isEqualTo(userRegisterRequest.getPassword());
        assertThat(captureUser.getEmail()).isEqualTo(userRegisterRequest.getEmail());
        assertThat(captureUser.getRealName()).isEqualTo(userRegisterRequest.getRealName());
        assertThat(captureUser.getGroup()).isEqualTo(userRegisterRequest.getGroup());
        for (int i = 0; i < 2; i++) {
            assertThat(captureUser.getRoles()).contains(roles.get(i));
        }

        verify(modelMapper).map(null, UserResponse.class);

    }

    
    @Test
    void canGetAllUsers(){
        List<User> users = new ArrayList<>();
        List<UserResponse> userResponses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            users.add(new User());
            userResponses.add(setUpUserResponse(
                    users.get(i),
                    "id", "username", "group", List.of(new String[]{"id1", "id2"})
            ));
        }
        given(userRepository.findAll()).willReturn(users);

        List<UserResponse> result = userService.getAllUsers();

        verify(userRepository).findAll();
        for (int i = 0; i < 2; i++) {
            assertUserResponseResult(result.get(i), userResponses.get(i));
            verify(modelMapper).map(users.get(i), UserResponse.class);
        }

    }

    @Test
    void canGetUserById(){
        String id = "id";
        User user = new User();
        Optional<User> userData = Optional.of(user);
        given(userRepository.findById(id)).willReturn(userData);

        UserResponse userResponse = setUpUserResponse(user,
                id, "username", "group", List.of(new String[]{"id2"})
        );

        UserResponse result = userService.getUserById(id);
        assertUserResponseResult(result, userResponse);

        verify(userRepository).findById(id);
        verify(modelMapper).map(user, UserResponse.class);
    }

    @Test
    void canGetAllUsersByGroup(){
        String group = "group";
        List<User> users = new ArrayList<>();
        List<UserResponse> userResponses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setGroup(group);
            users.add(user);
            userResponses.add(setUpUserResponse(users.get(i),
                    "id", "username", group, List.of(new String[]{"id1", "id2"})));
        }
        given(userRepository.findByGroup(group)).willReturn(users);

        List<UserResponse> result = userService.getAllUsersByGroup(group);

        verify(userRepository).findByGroup(group);
        for (int i = 0; i < 2; i++) {
            verify(modelMapper).map(users.get(i), UserResponse.class);
            assertUserResponseResult(result.get(i), userResponses.get(i));
        }
    }

    @Test
    void canUpdateUser(){
        String id = "id";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("password",
                "name", "group");

        Optional<User> userData = Optional.of(new User());
        given(userRepository.findById(id)).willReturn(userData);

        UserResponse userResponse = setUpUserResponse(null,
                id, "username", userUpdateRequest.getGroup(), List.of(new String[]{"id2"})
        );

        UserResponse result = userService.updateUser(id, userUpdateRequest);
        assertUserResponseResult(result, userResponse);

        verify(userRepository).findById(id);
        verify(encoder).encode(userUpdateRequest.getPassword());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User captureUser = userArgumentCaptor.getValue();
        assertThat(captureUser.getPassword()).isNull();
        assertThat(captureUser.getRealName()).isEqualTo(userUpdateRequest.getRealName());
        assertThat(captureUser.getGroup()).isEqualTo(userUpdateRequest.getGroup());

        verify(modelMapper).map(null, UserResponse.class);

    }

    @Test
    void canDeleteUser(){
        String userId = "id1";
        String lessonId = "id2";
        User user = new User();
        List<String> lessonsId = new ArrayList<>();
        lessonsId.add(lessonId);
        user.setLessonsId(lessonsId);
        Optional<User> userData = Optional.of(user);
        given(userRepository.findById(userId)).willReturn(userData);

        Lesson lesson = new Lesson();
        List<String> usersId = new ArrayList<>();
        usersId.add(userId);
        lesson.setUsersId(usersId);
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(user.getLessonsId().get(0))).willReturn(lessonData);

        HttpStatus httpStatus = userService.deleteUser(userId);
        assertThat(httpStatus).isEqualTo(HttpStatus.NO_CONTENT);

        verify(userRepository).findById(userId);
        verify(lessonRepository).findById(lessonId);

        ArgumentCaptor<Lesson> lessonArgumentCaptor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository).save(lessonArgumentCaptor.capture());
        Lesson captureLesson = lessonArgumentCaptor.getValue();
        assertThat(captureLesson.getUsersId()).isEmpty();

        verify(userRepository).deleteById(userId);

    }

    @Test
    void canGetAllLessonFromUser(){
        String userId = "id1";
        String lessonId = "id2";
        User user = new User();
        List<String> lessonsId = new ArrayList<>();
        lessonsId.add(lessonId);
        user.setLessonsId(lessonsId);
        Optional<User> userData = Optional.of(user);
        given(userRepository.findById(userId)).willReturn(userData);

        Lesson lesson = new Lesson();
        Optional<Lesson> lessonData = Optional.of(lesson);
        given(lessonRepository.findById(user.getLessonsId().get(0))).willReturn(lessonData);

        List<LessonResponse> lessonResponses = new ArrayList<>();
        lessonResponses.add(new LessonResponse(lessonId,
                "name", "group",
                List.of(new String[]{userId})));
        given(modelMapper.map(lesson, LessonResponse.class)).willReturn(
                new LessonResponse(lessonId,
                        "name", "group",
                        List.of(new String[]{userId}))
        );

        List<LessonResponse> result = userService.getAllLessonFromUser(userId);
        assertThat(result.get(0).getId()).isEqualTo(lessonResponses.get(0).getId());
        assertThat(result.get(0).getName()).isEqualTo(lessonResponses.get(0).getName());
        assertThat(result.get(0).getDescription()).isEqualTo(lessonResponses.get(0).getDescription());
        for (String resultUserId: result.get(0).getUsersId()) {
            assertThat(resultUserId).isEqualTo(userId);
        }

        verify(lessonRepository).findById(lessonId);
        verify(modelMapper).map(lesson, LessonResponse.class);

    }
    @Test
    void willThrowWhenUsernameIsTaken() {
        //given
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("username");
        given(userRepository.existsByUsername(userRegisterRequest.getUsername()))
                .willReturn(Boolean.TRUE);
        //when
        assertThatThrownBy(() -> userService.createUser(userRegisterRequest))
                .isInstanceOf(CustomInvalidParameterException.class).
                hasMessageContaining("username.exist", "username");
        verify(userRepository, never()).save(any());

    }
    @Test
    void willThrowWhenEmailIsTaken() {
        //given

        String email = "email";
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail(email);
        given(userRepository.existsByEmail(email))
                .willReturn(Boolean.TRUE);
        //when
        assertThatThrownBy(() -> userService.createUser(userRegisterRequest))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("email.exist", "email");
        verify(userRepository, never()).save(any());

    }
    @Test
    void willThrowWhenRolesIsEmpty() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        Set<String> strRoles = new HashSet<>();
        userRegisterRequest.setRoles(strRoles);
        assertThatThrownBy(()->userService.createUser(userRegisterRequest))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("roles.notBlank", "roles");
    }
    @Test
    void willThrowWhenRolesIsInvalid() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        Set<String> strRoles = new HashSet<>();
        strRoles.add("mmm");
        userRegisterRequest.setRoles(strRoles);
        assertThatThrownBy(()->userService.createUser(userRegisterRequest))
                .isInstanceOf(InvalidRoleException.class);
    }

    @Test
    void willThrowWhenUserNotFoundById(){
        String id = "id";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("password", "name", "group");

        assertThatThrownBy(()->userService.getUserById(id))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("user.id.notFound", "id");
        assertThatThrownBy(()-> userService.updateUser(id, userUpdateRequest))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("user.id.notFound", "id");
        assertThatThrownBy(()-> userService.deleteUser(id))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("user.id.notFound", "id");
        assertThatThrownBy(()-> userService.getAllLessonFromUser(id))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("user.id.notFound", "id");
    }
    @Test
    void willThrowWhenUsersNotFoundByGroup(){
        assertThatThrownBy(()->userService.getAllUsersByGroup("group"))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("user.group.notFound", "group");
    }

    @Test
    void willThrowWhenLessonsIdIsEmptyInUser(){
        String userId = "id";
        User user = new User();
        user.setLessonsId(new ArrayList<>());
        Optional<User> userData = Optional.of(user);
        given(userRepository.findById(userId)).willReturn(userData);

        assertThatThrownBy(()->userService.getAllLessonFromUser(userId))
                .isInstanceOf(CustomInvalidParameterException.class)
                .hasMessageContaining("user.lessonsId.isEmpty", "lessonsId");
    }

}
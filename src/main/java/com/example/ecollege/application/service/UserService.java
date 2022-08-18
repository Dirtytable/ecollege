package com.example.ecollege.application.service;

import com.example.ecollege.api.core.model.Lesson;
import com.example.ecollege.api.core.repository.LessonRepository;
import com.example.ecollege.api.exceptions.CustomInvalidParameterException;
import com.example.ecollege.api.exceptions.InvalidRoleException;
import com.example.ecollege.application.playoad.request.UserRegisterRequest;
import com.example.ecollege.api.core.model.ERole;
import com.example.ecollege.api.core.model.Role;
import com.example.ecollege.api.core.model.User;
import com.example.ecollege.api.core.repository.RoleRepository;
import com.example.ecollege.api.core.repository.UserRepository;
import com.example.ecollege.application.playoad.request.UserUpdateRequest;
import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;


@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private LessonRepository lessonRepository;
    private PasswordEncoder encoder;
    private ModelMapper modelMapper;

    @Bean
    private ModelMapper getModelMapper(){
        return new ModelMapper();
    }
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, LessonRepository lessonRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.lessonRepository = lessonRepository;
        this.encoder = encoder;
        this.modelMapper = getModelMapper();
    }

    private void checkUserWithId(Optional<User> userData){
        if (userData.isEmpty()){
            throw new CustomInvalidParameterException("user.id.notFound", "id");
        }
    }

    //Создать пользователя
    public UserResponse createUser(UserRegisterRequest userRegisterRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userRegisterRequest.getUsername()))){
            throw new CustomInvalidParameterException("username.exist", "username");
        }
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRegisterRequest.getEmail()))){
            throw new CustomInvalidParameterException("email.exist", "email");
        }

        User user = new User(userRegisterRequest.getUsername(),
                encoder.encode(userRegisterRequest.getPassword()),
                userRegisterRequest.getEmail(), userRegisterRequest.getRealName(),
                userRegisterRequest.getGroup());

        user.setRoles(getRoles(userRegisterRequest.getRoles()));
        return modelMapper.map(userRepository.save(user), UserResponse.class);

    }
    //Получить роль
    private Set<Role> getRoles(Set<String> strRoles)  {
        Set<Role> roles = new HashSet<>();
        if (strRoles.isEmpty()) {
            throw new CustomInvalidParameterException("roles.notBlank", "roles");
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
                        roles.add(adminRole);
                        break;
                    case "user":
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER);
                        roles.add(userRole);
                        break;
                    default:
                        throw new InvalidRoleException();
                }
            });
        }
        return roles;
    }

    public List<UserResponse> getAllUsers() {
        List<User>  users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user: users) {
            userResponses.add(modelMapper.map(user, UserResponse.class));
        }
        return userResponses;
    }

    public List<UserResponse> getAllUsersByGroup(String group) {

        List<User> users = userRepository.findByGroup(group);
        if (users.isEmpty()){
            throw new CustomInvalidParameterException("user.group.notFound", "group");
        }
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user: users) {
            userResponses.add(modelMapper.map(user, UserResponse.class));
        }
        return userResponses;
    }

    public UserResponse getUserById(String id) {
        Optional<User> userData = userRepository.findById(id);
        checkUserWithId(userData);
        return modelMapper.map(userData.get(), UserResponse.class);
    }

    public UserResponse updateUser(String id, UserUpdateRequest newUserData) {
        Optional<User> oldUserData = userRepository.findById(id);
        checkUserWithId(oldUserData);
        User user = oldUserData.get();
        user.setPassword(encoder.encode(newUserData.getPassword()));
        user.setRealName(newUserData.getRealName());
        user.setGroup(newUserData.getGroup());
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    public HttpStatus deleteUser(String id) {
        Optional<User> userData = userRepository.findById(id);
        checkUserWithId(userData);
        User user = userData.get();
        if (!user.getLessonsId().isEmpty()){
            //Удаление пользователя в уроках
            for (int i = 0; i < user.getLessonsId().size(); i++) {
                Optional<Lesson> lessonData = lessonRepository.findById(user.getLessonsId().get(i));
                if (lessonData.isPresent()) {
                    Lesson lesson = lessonData.get();
                    lesson.getUsersId().remove(id);
                    lessonRepository.save(lesson);
                }
            }
        }
        userRepository.deleteById(id);
        return HttpStatus.NO_CONTENT;

    }

    public List<LessonResponse> getAllLessonFromUser(String userId) {
        Optional<User> userData = userRepository.findById(userId);
        checkUserWithId(userData);
        User user = userData.get();
        if (user.getLessonsId().isEmpty()){
            throw new CustomInvalidParameterException("user.lessonsId.isEmpty", "lessonsId");
        }
        List<LessonResponse> lessonResponses = new ArrayList<>();
        for (int i = 0; i < user.getLessonsId().size(); i++) {
            Optional<Lesson> lessonData = lessonRepository.findById(user.getLessonsId().get(i));
            if (lessonData.isPresent()) {
                Lesson lesson = lessonData.get();
                lessonResponses.add(modelMapper.map(lesson, LessonResponse.class));
            }
        }

        return lessonResponses;

    }
}

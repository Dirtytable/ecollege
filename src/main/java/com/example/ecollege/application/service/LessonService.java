package com.example.ecollege.application.service;

import com.example.ecollege.api.core.model.Lesson;
import com.example.ecollege.api.core.model.User;
import com.example.ecollege.api.core.repository.LessonRepository;
import com.example.ecollege.api.core.repository.UserRepository;
import com.example.ecollege.api.exceptions.CustomInvalidParameterException;
import com.example.ecollege.application.playoad.request.LessonRequest;
import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.StringContent;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LessonService {
    LessonRepository lessonRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Bean
    public ModelMapper getMapper() {
        return new ModelMapper();
    }

    @Autowired
    public LessonService(LessonRepository lessonRepository, UserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.modelMapper = getMapper();
    }

    private void checkLessonWithId(Optional<Lesson> optional){
        if (optional.isEmpty()){
            throw new CustomInvalidParameterException("lesson.id.notFound", "id");
        }
    }

    public LessonResponse createLesson(LessonRequest lessonRequest) {
        Lesson lesson = new Lesson(lessonRequest.getName(),lessonRequest.getDescription());
        return modelMapper.map(lessonRepository.save(lesson), LessonResponse.class);
    }

    public List<LessonResponse> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        List<LessonResponse> lessonResponses = new ArrayList<>();
        for (Lesson lesson: lessons) {
            lessonResponses.add(modelMapper.map(lesson, LessonResponse.class));
        }
        return lessonResponses;
    }

    public LessonResponse getLessonById(String id) {
        Optional<Lesson> lessonData = lessonRepository.findById(id);
        checkLessonWithId(lessonData);
        return modelMapper.map(lessonData.get(), LessonResponse.class);
    }
    public LessonResponse updateLesson(String id, LessonRequest newLessonData) {
        Optional<Lesson> oldLessonData = lessonRepository.findById(id);
        checkLessonWithId(oldLessonData);
        Lesson lesson = oldLessonData.get();
        lesson.setName(newLessonData.getName());
        lesson.setDescription(newLessonData.getDescription());
        return modelMapper.map(lessonRepository.save(lesson), LessonResponse.class);
    }

    public List<UserResponse> getAllUsersFromLesson(String lessonId) {
        Optional<Lesson> lessonData = lessonRepository.findById(lessonId);
        checkLessonWithId(lessonData);
        Lesson lesson = lessonData.get();
        if (lesson.getUsersId().isEmpty()){
            throw new CustomInvalidParameterException("lesson.usersId.isEmpty", "usersId");
        }
        List<UserResponse> users = new ArrayList<>();
        for (String userId: lesson.getUsersId()) {
            Optional<User> userData = userRepository.findById(userId);
            userData.ifPresent(user -> users.add(modelMapper.map(user, UserResponse.class)));
        }
        return users;

    }

    public HttpStatus deleteLesson(String id) {
        Optional<Lesson> lessonData = lessonRepository.findById(id);
        checkLessonWithId(lessonData);
        Lesson lesson = lessonData.get();
        if (!lesson.getUsersId().isEmpty()){
            //Удаление уроков у пользователях
            for (int i = 0; i < lesson.getUsersId().size(); i++) {
                Optional<User> userData = userRepository.findById(lesson.getUsersId().get(i));
                if (userData.isPresent()) {
                    User user = userData.get();
                    user.getLessonsId().remove(lesson.getId());
                    userRepository.save(user);
                }
            }
        }
        lessonRepository.deleteById(id);

        return HttpStatus.NO_CONTENT;
    
    }

    public LessonResponse putUserToLessonByName(String lessonId, String username) {
        Optional<Lesson> lessonData = lessonRepository.findById(lessonId);
        checkLessonWithId(lessonData);
        
        User user = userRepository.findByUsername(username);    
        Lesson lesson = lessonData.get();
        if (lesson.getUsersId().contains(user.getId())){
            throw new CustomInvalidParameterException("lesson.usersId.userAlreadyAdded", "username");
        }
        lesson.getUsersId().add(user.getId());
        user.getLessonsId().add(lesson.getId());
        userRepository.save(user);
        return modelMapper.map(lessonRepository.save(lesson), LessonResponse.class);
    }

    public LessonResponse deleteUserFromLesson(String lessonId, String username) {
        Optional<Lesson> lessonData = lessonRepository.findById(lessonId);
        checkLessonWithId(lessonData);
        if (Boolean.TRUE.equals(!userRepository.existsByUsername(username))){
            throw new CustomInvalidParameterException("user.username.notFound", "username");

        }
        User user = userRepository.findByUsername(username);
        String userId = user.getId();
        Lesson lesson = lessonData.get();
        if (!lesson.getUsersId().contains(userId)){
            throw new CustomInvalidParameterException("lesson.usersId.notFound", "userId");
        }
        lesson.getUsersId().remove(userId);
        user.getLessonsId().remove(lessonId);
        userRepository.save(user);
        return modelMapper.map(lessonRepository.save(lesson), LessonResponse.class);
    }
}

package com.example.ecollege.api.controllers;

import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import com.example.ecollege.application.service.LessonService;
import com.example.ecollege.application.playoad.request.LessonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class LessonController {
    LessonService lessonService;


    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    //Добавить урок в базу
    @PostMapping("/lesson")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> postLesson(@RequestHeader(name = "Accept-Language", required = false) final Locale locale, @Valid @RequestBody LessonRequest lessonRequest){
        return new ResponseEntity<>(lessonService.createLesson(lessonRequest), HttpStatus.CREATED);
    }
    //Показать все уроки
    @GetMapping("/lessons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LessonResponse>> getAllLessons(@RequestHeader(name = "Accept-Language", required = false) final Locale locale) {
        return  new ResponseEntity<>(lessonService.getAllLessons(), HttpStatus.FOUND);
    }
    //Показать урок по айди
    @GetMapping("/lessons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> getLessonById(@RequestHeader(name = "Accept-Language", required = false) final Locale locale,@PathVariable("id") String id) {
        return new ResponseEntity<>(lessonService.getLessonById(id), HttpStatus.FOUND);
    }
    //Обновить урок
    @PutMapping("/lessons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> updateLesson(@RequestHeader(name = "Accept-Language", required = false) final Locale locale, @PathVariable("id") String id, @Valid @RequestBody LessonRequest newLessonData){
        return new ResponseEntity<>(lessonService.updateLesson(id, newLessonData), HttpStatus.OK);
    }
    //Показать всех пользователей в уроке
    @GetMapping("/lessons/{id}/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUserFromLesson(@RequestHeader(name = "Accept-Language", required = false) final Locale locale, @PathVariable("id") String lessonId){
        return new ResponseEntity<>(lessonService.getAllUsersFromLesson(lessonId), HttpStatus.FOUND);
    }
    //Удалить урок
    @DeleteMapping("/lessons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteLesson(@RequestHeader(name = "Accept-Language", required = false) final Locale locale,@PathVariable("id") String id) {
        return new ResponseEntity<>(lessonService.deleteLesson(id));

    }
    //Добавить пользователя в урок
    @PutMapping("/lessons/{id}/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> putUserToLessonByName(@RequestHeader(name = "Accept-Language", required = false) final Locale locale,@PathVariable("id") String lessonId,@Valid @RequestParam() String username){
        return new ResponseEntity<>(lessonService.putUserToLessonByName(lessonId, username), HttpStatus.OK);

    }

    //Удалить пользователя из урока
    @DeleteMapping("/lessons/{id}/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> deleteUserFromLesson(@RequestHeader(name = "Accept-Language", required = false) final Locale locale,@PathVariable("id") String lessonId,@Valid @RequestParam String username){
        return new ResponseEntity<>(lessonService.deleteUserFromLesson(lessonId, username), HttpStatus.OK);

    }
}

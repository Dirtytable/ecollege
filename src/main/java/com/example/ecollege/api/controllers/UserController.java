package com.example.ecollege.api.controllers;

import com.example.ecollege.application.playoad.request.UserUpdateRequest;
import com.example.ecollege.application.playoad.responce.LessonResponse;
import com.example.ecollege.application.playoad.responce.UserResponse;
import com.example.ecollege.application.service.UserService;
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
public class UserController {


    UserService userService;
    @Autowired

    public UserController(UserService userService) {

        this.userService = userService;
    }




    //Показать всех пользователей
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestHeader(name = "Accept-Language", required = false) final Locale locale) {
       return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
    //Показать пользователей по группе
    @GetMapping("/users/groups/{group}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsersByGroup(@RequestHeader(name = "Accept-Language", required = false) final Locale locale,@PathVariable("group") String group) {
        return new ResponseEntity<>(userService.getAllUsersByGroup(group), HttpStatus.OK);

    }
    //полказать пользователей по айди
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@RequestHeader(name = "Accept-Language", required = false) final Locale locale, @PathVariable("id") String id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    //Обновить пользователя
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@RequestHeader(name = "Accept-Language", required = false) final Locale locale, @PathVariable("id") String id, @Valid @RequestBody UserUpdateRequest newUserData) {
        return new ResponseEntity<>(userService.updateUser(id, newUserData), HttpStatus.OK);
    }
    //Удалить пользователя
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUser(@RequestHeader(name = "Accept-Language", required = false) final Locale locale,
                                                 @PathVariable("id") String id) {
        return new ResponseEntity<>(userService.deleteUser(id));
    }

    //Показать все уроки у пользователя
    @GetMapping("/users/{id}/lessons")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<LessonResponse>> getAllLessonFromUser(@RequestHeader(name = "Accept-Language", required = false) final Locale locale,
                                                                     @PathVariable("id") String userId){
        return new ResponseEntity<>(userService.getAllLessonFromUser(userId), HttpStatus.FOUND);
    }

}

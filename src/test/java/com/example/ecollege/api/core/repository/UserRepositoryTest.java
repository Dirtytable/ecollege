package com.example.ecollege.api.core.repository;

import com.example.ecollege.api.core.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class UserRepositoryTest {

    private UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //    User findByUsername(String username);
    @Test
    void canFindByUsername() {
        String expected = "username";
        //given
        User user = new User();
        user.setUsername(expected);
        userRepository.save(user);
        //when
        User result = userRepository.findByUsername(user.getUsername());
        //Then
        assertThat(result.getUsername()).isEqualTo(expected);
    }

//    List<User> findByGroup(String group);
    @Test
    void canFindByGroup(){
        String expected = "group";
        User user = new User();
        user.setGroup("asd");
        userRepository.save(user);
        for (int i = 0; i < 2; i++) {
            user = new User();
            user.setGroup(expected);
            userRepository.save(user);
        }
        //when
        List<User> result = userRepository.findByGroup(expected);
        //Then
        assertThat(result).hasSize(2);
        for (User resultUser:result) {
            assertThat(resultUser.getGroup()).isEqualTo(expected);
        }
    }
//    Boolean existsByUsername(String username);
    @Test
    void canExistByUsername(){
        String username = "username";
        User user = new User();
        user.setUsername(username);
        userRepository.save(user);
        Boolean result = userRepository.existsByUsername(user.getUsername());
        assertThat(result).isTrue();
    }
    
//    Boolean existsByEmail(String email);

    @Test
    void canExistByEmail(){
        String email = "email@gmail.com";
        User user = new User();
        user.setEmail(email);
        userRepository.save(user);
        Boolean result = userRepository.existsByEmail(user.getEmail());
        assertThat(result).isTrue();
    }

}
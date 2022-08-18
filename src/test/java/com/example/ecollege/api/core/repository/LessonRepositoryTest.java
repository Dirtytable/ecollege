package com.example.ecollege.api.core.repository;

import com.example.ecollege.api.core.model.Lesson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class LessonRepositoryTest {

    LessonRepository lessonRepository;

    @Autowired
    public LessonRepositoryTest(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Test
    void canFindAllByName() {
        String expected = "name";
        Lesson lesson = new Lesson();
        lesson.setName(" ");
        lessonRepository.save(lesson);
        for (int i = 0; i < 2; i++) {
            lesson = new Lesson();
            lesson.setName(expected);
            lessonRepository.save(lesson);
        }
        List<Lesson> result = lessonRepository.findAllByName(expected);
        assertThat(result).hasSize(2);
        for (Lesson resultLesson: result) {
            assertThat(resultLesson.getName()).isEqualTo(expected);
        }
    }
}
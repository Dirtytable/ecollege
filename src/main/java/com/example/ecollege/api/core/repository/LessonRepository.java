package com.example.ecollege.api.core.repository;

import com.example.ecollege.api.core.model.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
    List<Lesson> findAllByName(String name);
}

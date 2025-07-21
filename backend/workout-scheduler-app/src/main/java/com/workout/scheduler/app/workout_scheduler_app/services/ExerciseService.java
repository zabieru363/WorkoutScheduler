package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.ExerciseDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Exercise;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Set;

public interface ExerciseService {
    void save(Exercise exercise);
    Exercise findExerciseById(int id);
    Set<Exercise> findExercisesByIds(Set<Integer> ids);
    List<ExerciseDTO> findExercisesByName(String name);
    ExerciseDTO getExerciseById(Integer id);
    String createCustomExercise(String data, List<MultipartFile> imagesRequest);
    String updateCustomExercise(Integer id, String data, List<MultipartFile> imagesRequest);
    String deleteCustomExercise(Integer id);
}
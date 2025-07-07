package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.ExerciseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public interface ExerciseService {
    List<ExerciseDTO> findExercisesByName(String name);
    ExerciseDTO getExerciseById(Integer id);
    String createCustomExercise(String data, List<MultipartFile> imagesRequest);
    String updateCustomExercise(Integer id, String data, List<MultipartFile> imagesRequest);
    String deleteCustomExercise(Integer id);
}
package com.workout.scheduler.app.workout_scheduler_app.mappers;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.ExerciseDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewCustomExerciseDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Exercise;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.ExerciseImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    Exercise exerciseFromNewExerciseDTO(NewCustomExerciseDTO newCustomExerciseDTO);

    @Mapping(target = "imagesUrls", source = "images")
    ExerciseDTO exerciseDTOFromExercise(Exercise exercise);

    List<ExerciseDTO> exerciseDTOListFromExerciseList(List<Exercise> exercises);

    // Para mapear la lista de imágenes:
    default List<String> mapImages(List<ExerciseImage> images) {
        if (images == null) return List.of();
        return images.stream()
                .map(ExerciseImage::getUrl)
                .toList();
    }
}
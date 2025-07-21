package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import java.util.List;

public record ExerciseDTO(
        Integer id,
        String name,
        String mainMuscle,
        String secondaryMuscle,
        String description,
        Boolean requireEquipment,
        Boolean isCustom,
        String videoURL,
        List<String> imagesUrls
){}
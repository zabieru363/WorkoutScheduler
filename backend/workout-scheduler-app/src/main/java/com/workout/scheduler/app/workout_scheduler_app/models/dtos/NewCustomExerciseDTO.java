package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

public record NewCustomExerciseDTO(
        String name,
        String mainMuscle,
        String secondaryMuscle,
        String description,
        Boolean requireEquipment,
        String videoURL
){}
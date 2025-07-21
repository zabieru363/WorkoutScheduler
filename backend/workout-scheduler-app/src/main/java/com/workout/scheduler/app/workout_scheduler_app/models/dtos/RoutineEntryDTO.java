package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

public record RoutineEntryDTO(
        ExerciseDTO exercise,
        Integer sets,
        Integer reps,
        Integer restSeconds,
        String notes
) {}
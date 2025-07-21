package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import jakarta.validation.constraints.NotNull;

public record NewRoutineEntryDTO(
        @NotNull Integer exerciseId,
        @NotNull Integer sets,
        @NotNull Integer reps,
        Integer restSeconds,
        String notes
) {}
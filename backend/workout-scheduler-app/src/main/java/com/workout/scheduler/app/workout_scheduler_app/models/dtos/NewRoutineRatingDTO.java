package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import jakarta.validation.constraints.NotNull;

public record NewRoutineRatingDTO(
        @NotNull Integer stars,
        String comment
) {}
package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record NewRoutineDTO(
        @NotBlank String name,
        @Valid Set<NewRoutineEntryDTO> exercises
) {}
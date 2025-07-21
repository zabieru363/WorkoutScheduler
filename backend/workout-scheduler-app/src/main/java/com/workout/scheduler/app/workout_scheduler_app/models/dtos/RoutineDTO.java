package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import java.util.Set;

public record RoutineDTO(
        Integer id,
        String name,
        Set<RoutineEntryDTO> exercises
) {}
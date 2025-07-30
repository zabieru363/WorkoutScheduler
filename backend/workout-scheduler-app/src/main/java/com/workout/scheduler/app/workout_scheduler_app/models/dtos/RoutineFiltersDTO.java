package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public record RoutineFiltersDTO(
        String name,
        String mainMuscle,
        String secondaryMuscle,
        LocalDateTime before,
        LocalDateTime after,
        LocalDateTime[] dates,
        Set<String> exercises,
        Boolean mostPopular,
        PageRequestDTO pageRequest
) {}
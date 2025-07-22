package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import java.time.LocalDateTime;

public record RoutineRatingDTO(
        Integer id,
        Integer stars,
        String comment,
        Integer routineId,
        Integer createdBy,
        LocalDateTime modifiedAt
){}
package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

public record PageRequestDTO(
        int page,
        int size,
        String orderField,
        String direction
) {}
package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

public record LoginRequestDTO(
        String usernameOrEmail,
        String password
){}
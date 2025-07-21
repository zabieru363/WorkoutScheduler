package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import java.util.HashMap;
import java.util.Map;

public record NewEmailDTO(
        String to,
        String subject,
        String message,
        Map<String, Object> params
) {
    public NewEmailDTO(String to, String subject, String message) {
        this(to, subject, message, new HashMap<>()); // Llama al constructor canónico con un HashMap vacío
    }
}
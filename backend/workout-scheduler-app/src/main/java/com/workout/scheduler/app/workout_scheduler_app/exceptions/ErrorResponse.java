package com.workout.scheduler.app.workout_scheduler_app.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final int statusCode;
    private final String message;
    private final LocalDateTime timestamp;
    private final String path; // Nuevo campo
}

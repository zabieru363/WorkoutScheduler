package com.workout.scheduler.app.workout_scheduler_app.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class GlobalException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final String path;
}
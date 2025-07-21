package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import com.workout.scheduler.app.workout_scheduler_app.enums.EPersonType;
import java.time.LocalDateTime;

public record UserDataDTO(
        Integer id,
        String username,
        String email,
        LocalDateTime createdAt,
        String name,
        String lastname,
        String phone,
        Double height,
        Double weight,
        EPersonType personType,
        byte trainings,
        LocalDateTime birthdate
) {}
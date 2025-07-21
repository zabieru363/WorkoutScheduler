package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDateTime;

public record NewUserDTO(
        @NotNull @NotBlank String username,
        @NotNull @NotBlank String password,
        @NotNull @NotBlank @Email String email,
        @NotNull @NotBlank String personType,
        @NotNull @NotBlank @Length(min = 5) String name,
        @NotNull @NotBlank @Length(min = 5) String lastname,
        @NotNull @NotBlank String phone,
        Double height,
        Double weight,
        @NotNull LocalDateTime birthdate,
        byte trainings
) {}

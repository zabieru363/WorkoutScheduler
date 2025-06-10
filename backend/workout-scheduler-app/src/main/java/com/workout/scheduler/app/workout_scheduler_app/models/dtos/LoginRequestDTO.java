package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginRequestDTO {
    private String usernameOrEmail;
    private String password;
}
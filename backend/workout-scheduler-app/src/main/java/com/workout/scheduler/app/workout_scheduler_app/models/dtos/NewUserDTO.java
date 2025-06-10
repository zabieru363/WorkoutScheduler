package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewUserDTO {
    private String username;
    private String password;
    private String email;
    private String personType;
    private String name;
    private String lastname;
    private String phone;
    private Double height;
    private Double weight;
    private LocalDateTime birthdate;
    private byte trainings;
}

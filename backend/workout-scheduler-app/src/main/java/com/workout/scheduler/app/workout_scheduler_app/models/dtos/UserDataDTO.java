package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import com.workout.scheduler.app.workout_scheduler_app.enums.EPersonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDataDTO {

    private Integer id;
    private String username;
    private String email;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private String name;
    private String lastname;
    private String phone;
    private Double height;
    private Double weight;
    private EPersonType personType;
    private byte trainings;
    private LocalDateTime birthdate;
}
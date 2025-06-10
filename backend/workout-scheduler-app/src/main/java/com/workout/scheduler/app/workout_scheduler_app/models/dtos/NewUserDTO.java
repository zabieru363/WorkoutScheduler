package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewUserDTO {
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private String password;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String personType;
    @NotNull
    @NotBlank
    @Length(min = 5)
    private String name;
    @NotNull
    @NotBlank
    @Length(min = 5)
    private String lastname;
    @NotNull
    @NotBlank
    private String phone;
    private Double height;
    private Double weight;
    @NotNull
    private LocalDateTime birthdate;
    private byte trainings;
}

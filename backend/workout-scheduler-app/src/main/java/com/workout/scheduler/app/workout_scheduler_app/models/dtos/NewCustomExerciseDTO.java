package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewCustomExerciseDTO implements Serializable {
    private String name;
    private String mainMuscle;
    private String secondaryMuscle;
    private String description;
    private Boolean requireEquipment;
    private String videoURL;
}
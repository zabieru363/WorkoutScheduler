package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExerciseDTO {
    private Integer id;
    private String name;
    private String mainMuscle;
    private String secondaryMuscle;
    private String description;
    private Boolean requireEquipment;
    private Boolean isCustom;
    private String videoURL;
    private List<String> imagesUrls;
}
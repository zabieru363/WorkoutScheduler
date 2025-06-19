package com.workout.scheduler.app.workout_scheduler_app.models.dtos;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class NewEmailDTO {
    private String to;
    private String subject;
    private String message;
    private Map<String, Object> params = new HashMap<>();

    public NewEmailDTO(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }
}
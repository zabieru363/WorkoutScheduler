package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    String createUser(NewUserDTO data);
}
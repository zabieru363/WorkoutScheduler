package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    boolean existsByUsernameOrEmail(String property, String value);
    String createUser(NewUserDTO data);
}
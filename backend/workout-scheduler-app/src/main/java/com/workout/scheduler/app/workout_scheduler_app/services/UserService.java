package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.UserDataDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    boolean existsByUsernameOrEmail(String property, String value);
    String preRegister(NewUserDTO data);
    String registerConfirmation(int userId, String attempt);
    String resendConfirmationCode(int userId);
    UserDataDTO getUserData();
}
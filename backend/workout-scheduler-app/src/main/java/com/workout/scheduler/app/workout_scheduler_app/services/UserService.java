package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.UserDataDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;

public interface UserService {
    void save(User user);
    boolean existsById(int id);
    User getUserById(int id);
    boolean existsByUsernameOrEmail(String property, String value);
    String preRegister(NewUserDTO data);
    String registerConfirmation(int userId, String attempt);
    String resendConfirmationCode(int userId);
    UserDataDTO getUserData();
}
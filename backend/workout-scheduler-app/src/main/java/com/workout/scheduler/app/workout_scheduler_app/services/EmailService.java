package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewEmailDTO;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Service
public interface EmailService {
    void sendGenericEmail(Supplier<NewEmailDTO> function, String templateName) throws MessagingException;
    void sendConfirmationCodeEmail(NewEmailDTO data) throws MessagingException;
}
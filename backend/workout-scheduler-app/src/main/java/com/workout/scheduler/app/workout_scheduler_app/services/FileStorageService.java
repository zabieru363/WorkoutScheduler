package com.workout.scheduler.app.workout_scheduler_app.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {
    String store(MultipartFile file);
}
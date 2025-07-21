package com.workout.scheduler.app.workout_scheduler_app.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file);
}
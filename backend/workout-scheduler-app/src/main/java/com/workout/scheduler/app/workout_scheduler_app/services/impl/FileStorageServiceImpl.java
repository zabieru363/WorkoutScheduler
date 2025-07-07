package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.services.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public String store(MultipartFile file) {
        try {
            if (!Files.exists(root)) Files.createDirectories(root);

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = root.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen");
        }
    }
}
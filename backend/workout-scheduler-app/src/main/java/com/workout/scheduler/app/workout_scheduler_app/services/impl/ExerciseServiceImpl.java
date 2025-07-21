package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.mappers.ExerciseMapper;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.ExerciseDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewCustomExerciseDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Exercise;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.ExerciseImage;
import com.workout.scheduler.app.workout_scheduler_app.repositories.ExerciseRepository;
import com.workout.scheduler.app.workout_scheduler_app.services.ExerciseService;
import com.workout.scheduler.app.workout_scheduler_app.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final FileStorageService fileStorageService;

    @Override
    public void save(Exercise exercise) {
        exerciseRepository.save(exercise);
    }

    @Override
    public Exercise findExerciseById(int id) {
        return exerciseRepository.getExerciseById(id).orElseThrow(() -> {
            log.error("Exercise with id {} not found", id);
            return new GlobalException(HttpStatus.NOT_FOUND, "No se encontró el ejercicio con id: " + id);
        });
    }

    @Override
    public Set<Exercise> findExercisesByIds(Set<Integer> ids) {
        return exerciseRepository.findByIdInAndEnabledTrue(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDTO> findExercisesByName(String name) {
        return exerciseMapper.exerciseDTOListFromExerciseList(exerciseRepository.findExercisesByName(name.toLowerCase()));
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseDTO getExerciseById(Integer id) {
        Exercise exercise = exerciseRepository.getExerciseById(id).orElseThrow(() -> {
            log.error("Exercise with id {} not found", id);
            return new GlobalException(HttpStatus.NOT_FOUND, "No se encontró el ejercicio con id: " + id);
        });

        return exerciseMapper.exerciseDTOFromExercise(exercise);
    }

    private NewCustomExerciseDTO createNewCustomExerciseDTO(String data, boolean isUpdate) {
        NewCustomExerciseDTO dto;

        try {
            var objectMapper = new ObjectMapper();
            dto = objectMapper.readValue(data, NewCustomExerciseDTO.class);
        } catch (Exception e) {
            log.error("Error parsing NewCustomExerciseDTO: {}", e.getMessage());
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Datos no válidos para crear el ejercicio");
        }

        if(isUpdate) return dto;

        if(dto.name().isEmpty()) {
            log.error("Exercise name is empty");
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El nombre del ejercicio no puede estar vacío");
        }

        if(dto.mainMuscle().isEmpty()) {
            log.error("Exercise mainMuscle is empty");
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El ejercicio debe de tener un músculo principal");
        }

        return dto;
    }

    private List<ExerciseImage> createExerciseImagesList(Exercise newExercise, List<MultipartFile> imagesRequest) {
        List<ExerciseImage> images = new ArrayList<>();

        for (MultipartFile file : imagesRequest) {
            String url = fileStorageService.store(file);
            ExerciseImage image = new ExerciseImage();
            image.setUrl(url);
            image.setExercise(newExercise);
            images.add(image);
        }

        newExercise.setImages(images);

        return images;
    }

    @Override
    @Transactional
    public String createCustomExercise(String data, List<MultipartFile> imagesRequest) {
        NewCustomExerciseDTO dto = createNewCustomExerciseDTO(data, false);

        Exercise newExercise = exerciseMapper.exerciseFromNewExerciseDTO(dto);
        newExercise.setIsCustom(true);

        if(!imagesRequest.isEmpty())
            newExercise.setImages(createExerciseImagesList(newExercise, imagesRequest));

        exerciseRepository.save(newExercise);

        return "Done";
    }

    @Override
    @Transactional
    public String updateCustomExercise(Integer id, String data, List<MultipartFile> imagesRequest) {
        NewCustomExerciseDTO dto = createNewCustomExerciseDTO(data, true);

        Exercise exercise = exerciseRepository.getExerciseById(id).orElseThrow(() -> {
            log.error("Exercise with id {} not found", id);
            return new GlobalException(HttpStatus.NOT_FOUND, "No se encontró el ejercicio con id: " + id);
        });

        if(dto.name() != null) exercise.setName(dto.name());
        if(dto.description() != null) exercise.setDescription(dto.description());
        if(dto.mainMuscle() != null) exercise.setMainMuscle(dto.mainMuscle());
        if(dto.secondaryMuscle() != null) exercise.setSecondaryMuscle(dto.secondaryMuscle());
        if(dto.requireEquipment() != null) exercise.setRequireEquipment(dto.requireEquipment());
        if(dto.videoURL() != null) exercise.setVideoURL(dto.videoURL());
        if(!imagesRequest.isEmpty()) exercise.setImages(createExerciseImagesList(exercise, imagesRequest));

        exerciseRepository.save(exercise);

        return "Done";
    }

    @Override
    @Transactional
    public String deleteCustomExercise(Integer id) {
        if(!exerciseRepository.existsByIdAndEnabledTrue(id)) {
            log.error("Exercise with id {} not found", id);
            throw new GlobalException(HttpStatus.NOT_FOUND, "No se encontró el ejercicio con id: " + id);
        }

        exerciseRepository.deleteCustomExercise(id);

        return "Done";
    }
}

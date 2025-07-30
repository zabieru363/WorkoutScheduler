package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.mappers.RoutineRatingMapper;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.RoutineRating;
import com.workout.scheduler.app.workout_scheduler_app.repositories.RoutineRatingRepository;
import com.workout.scheduler.app.workout_scheduler_app.security.SecurityContextHelper;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineRatingService;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutineRatingServiceImpl implements RoutineRatingService {

    private final RoutineService routineService;
    private final RoutineRatingRepository routineRatingRepository;
    private final RoutineRatingMapper routineRatingMapper;
    private final SecurityContextHelper securityContextHelper;
    private static final Logger logger = LoggerFactory.getLogger(RoutineRatingServiceImpl.class);

    @Override
    public void save(RoutineRating rating) {
        routineRatingRepository.save(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoutineRatingDTO> getAllRatingsOfRoutine(int routineId) {
        if(!routineService.routineExistsById(routineId)) {
            logger.error("Rutina con id: {} no encontrada", routineId);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Rutina no encontrada");
        }

        return routineRatingRepository.getAllRatingsOfRoutine(routineId);
    }

    @Override
    @Transactional
    public String createRoutineRating(int routineId, NewRoutineRatingDTO data) {
        Routine routine = routineService.findRoutineById(routineId);

        int currentUserId = securityContextHelper.getCurrentUserId();

        if(routineRatingRepository.existsByCreatedByAndEnabledTrue(currentUserId)) {
            logger.error("El usuario con id: {} ya ha valorado esta rutina.", currentUserId);
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya has hecho una valoración en esta rutina.");
        }

        RoutineRating rating = routineRatingMapper.routineRatingFromNewRoutineRatingDTO(data);

        rating.setRoutine(routine);
        rating.setCreatedBy(securityContextHelper.getCurrentUserId());

        save(rating);

        return "Valoración creada correctamente";
    }

    @Override
    @Transactional
    public String updateRoutineRating(int routineId, int ratingId, NewRoutineRatingDTO data) {
        if(!routineService.routineExistsById(routineId)) {
            logger.error("Rutina con id: {} no encontrada", routineId);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Rutina no encontrada");
        }

        RoutineRating rating = routineRatingRepository.findByIdAndEnabledTrue(ratingId).orElseThrow(() -> {
            logger.error("No existe una valoración con el id: {}", ratingId);
            return new GlobalException(HttpStatus.NOT_FOUND, "No existe una valoración con el id: " + ratingId);
        });

        if(!rating.getCreatedBy().equals(securityContextHelper.getCurrentUserId())) {
            logger.error("Solo el creador de la rutina puede actualizar esta valoración.");
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Solo el creador de la rutina puede actualizar esta valoración.");
        }

        if(data.stars() != null) rating.setStars(data.stars());
        if(data.comment() != null) rating.setComment(data.comment());

        rating.setModifiedAt(LocalDateTime.now());

        routineRatingRepository.save(rating);

        return "Valoración actualizada correctamente";
    }

    @Override
    @Transactional
    public String deleteRoutineRating(int routineId, int ratingId) {
        if(!routineService.routineExistsById(routineId)) {
            logger.error("Rutina con id: {} no encontrada", routineId);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Rutina no encontrada");
        }

        RoutineRating rating = routineRatingRepository.findByIdAndEnabledTrue(ratingId).orElseThrow(() -> {
            logger.error("No existe una valoración con el id: {}", ratingId);
            return new GlobalException(HttpStatus.NOT_FOUND, "No existe una valoración con el id: " + ratingId);
        });

        if(!rating.getCreatedBy().equals(securityContextHelper.getCurrentUserId())) {
            logger.error("Solo el creador de la rutina puede eliminar esta valoración.");
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Solo el creador de la rutina puede eliminar esta valoración.");
        }

        rating.setEnabled(false);
        routineRatingRepository.save(rating);

        return "Valoración eliminada correctamente";
    }
}

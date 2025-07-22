package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.RoutineRating;
import java.util.Set;

public interface RoutineRatingService {
    void save(RoutineRating rating);
    Set<RoutineRatingDTO> getAllRatingsOfRoutine(int routineId);
    String createRoutineRating(int routineId, NewRoutineRatingDTO data);
    String updateRoutineRating(int routineId, int ratingId, NewRoutineRatingDTO data);
    String deleteRoutineRating(int routineId, int ratingId);
}
package com.workout.scheduler.app.workout_scheduler_app.mappers;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.RoutineRating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoutineRatingMapper {
    RoutineRating routineRatingFromNewRoutineRatingDTO(NewRoutineRatingDTO newRoutineRatingDTO);
}
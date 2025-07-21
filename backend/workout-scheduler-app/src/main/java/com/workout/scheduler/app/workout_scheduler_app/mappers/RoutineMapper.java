package com.workout.scheduler.app.workout_scheduler_app.mappers;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import org.mapstruct.Mapper;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoutineMapper {
    Set<RoutineDTO> routineDTOListFromRoutineList(Set<Routine> routines);
    RoutineDTO routineDTOFromRoutine(Routine routine);
}
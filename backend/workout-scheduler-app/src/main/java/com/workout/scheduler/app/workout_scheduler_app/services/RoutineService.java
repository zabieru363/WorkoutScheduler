package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineFiltersDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import java.util.Set;

public interface RoutineService {
    void save(Routine routine);
    boolean routineExistsById(int id);
    Routine findRoutineById(int id);
    Set<RoutineDTO> searchRoutinesByFilters(RoutineFiltersDTO filters);
    Set<RoutineDTO> getUserRoutines(int userId);
    RoutineDTO getRoutineById(int id);
    String createRoutine(NewRoutineDTO data);
    String changeRoutineName(int id, String newName);
    String deleteRoutine(int id);
}
package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.enums.ERoutineListType;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.SavedRoutine;
import java.util.Set;

public interface SavedRoutineService {
    boolean isRoutineAlreadySavedByUser(int routineId, Set<SavedRoutine> savedRoutines, ERoutineListType listType);
    boolean isLoggedUserEqualsThanRoutineCreator(Integer currentUserId, Integer creatorRoutineId);
    Set<RoutineDTO> getAllRoutinesOfList(String listType);
    String saveRoutineInList(int routineId, String listType);
    String unsaveRoutineInList(int routineId, String listType);
}
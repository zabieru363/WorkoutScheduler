package com.workout.scheduler.app.workout_scheduler_app.services;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineEntryDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.RoutineEntry;
import java.util.Set;

public interface RoutineEntryService {
    Set<RoutineEntry> createRoutineEntries(Routine routine, Set<NewRoutineEntryDTO> exercisesRequest);
    String addExerciseToRoutine(int routineId, NewRoutineEntryDTO exerciseRequest);
    String changeExerciseInRoutine(int routineId, int exerciseId, NewRoutineEntryDTO exerciseRequest);
    String deleteExerciseFromRoutine(int routineId, int exerciseId);
}
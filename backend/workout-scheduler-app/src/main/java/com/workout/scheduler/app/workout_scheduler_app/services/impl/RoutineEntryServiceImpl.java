package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineEntryDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Exercise;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.RoutineEntry;
import com.workout.scheduler.app.workout_scheduler_app.services.ExerciseService;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineEntryService;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutineEntryServiceImpl implements RoutineEntryService {

    private final RoutineService routineService;
    private final ExerciseService exerciseService;
    private static final Logger logger = LoggerFactory.getLogger(RoutineEntryServiceImpl.class);

    /**
     * Crea la lista de ejercicios para una rutina. Este servicio es utilizado por el
     * servicio que crea una rutina.
     * @param routine La rutina a la que se le van a agregar los ejercicios.
     * @param exercisesRequest La lista de ejercicios que se le van a agregar a la rutina (con las
     * repeticiones, series, descanso y notas).
     * @return La lista de ejercicios que se le van a agregar a la rutina.
     */
    @Override
    public Set<RoutineEntry> createRoutineEntries(Routine routine, Set<NewRoutineEntryDTO> exercisesRequest) {
        Set<Exercise> exercises = exerciseService.findExercisesByIds(exercisesRequest.stream()
                .map(NewRoutineEntryDTO::exerciseId)
                .collect(Collectors.toSet()));

        Map<Integer, NewRoutineEntryDTO> exercisesRequestMap = exercisesRequest.stream()
                .collect(Collectors.toMap(
                        NewRoutineEntryDTO::exerciseId,
                        Function.identity()
                ));

        return exercises.stream()
                .map(e -> {
                    NewRoutineEntryDTO entryRequest = exercisesRequestMap.get(e.getId());

                    var entry = new RoutineEntry();
                    entry.setExercise(e);

                    entry.setReps(entryRequest.reps());
                    entry.setSets(entryRequest.sets());
                    entry.setRestSeconds(entryRequest.restSeconds());
                    entry.setNotes(entryRequest.notes());
                    entry.setRoutine(routine);

                    return entry;
                }).collect(Collectors.toSet());
    }

    private boolean exerciseExistsInRoutine(Routine routine, int exerciseId) {
        return routine.getExercises().stream()
                .anyMatch(e -> e.getExercise().getId().equals(exerciseId));
    }

    @Override
    @Transactional
    public String addExerciseToRoutine(int routineId, NewRoutineEntryDTO exerciseRequest) {
        Routine routine = routineService.findRoutineById(routineId);

        if(exerciseExistsInRoutine(routine, exerciseRequest.exerciseId())) {
            logger.error("El ejercicio con id {} ya existe en la rutina con id {}", exerciseRequest.exerciseId(), routineId);
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El ejercicio ya está añadido a la rutina");
        }

        var entry = new RoutineEntry();

        entry.setExercise(exerciseService.findExerciseById(exerciseRequest.exerciseId()));
        entry.setReps(exerciseRequest.reps());
        entry.setSets(exerciseRequest.sets());
        entry.setRestSeconds(exerciseRequest.restSeconds());
        entry.setNotes(exerciseRequest.notes());
        entry.setRoutine(routine);

        routine.getExercises().add(entry);

        routineService.save(routine);

        return "Nuevo ejercicio añadido correctamente.";
    }

    @Override
    @Transactional
    public String changeExerciseInRoutine(int routineId, int exerciseId, NewRoutineEntryDTO exerciseRequest) {
        Routine routine = routineService.findRoutineById(routineId);

        Set<RoutineEntry> exercises = routine.getExercises();

        RoutineEntry exercise = exercises.stream()
                .filter(e -> e.getExercise().getId().equals(exerciseId))
                .findFirst().orElseThrow(() -> {
                    logger.error("No se encontró el ejercicio con id {} en la rutina con id {}", exerciseId, routineId);
                    return new GlobalException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado en la rutina");
                });

        if(exerciseRequest.exerciseId() != null) {
            if(exerciseExistsInRoutine(routine, exerciseRequest.exerciseId())) {
                logger.error("El ejercicio con id {} ya existe en la rutina con id {}", exerciseRequest.exerciseId(), routineId);
                throw new GlobalException(HttpStatus.CONFLICT, "El ejercicio ya está añadido a la rutina");
            }

            exercise.setExercise(exerciseService.findExerciseById(exerciseRequest.exerciseId()));
        }

        if(exerciseRequest.reps() != null)
            exercise.setReps(exerciseRequest.reps());

        if(exerciseRequest.sets() != null)
            exercise.setSets(exerciseRequest.sets());

        if(exerciseRequest.restSeconds() != null)
            exercise.setRestSeconds(exerciseRequest.restSeconds());

        if(exerciseRequest.notes() != null)
            exercise.setNotes(exerciseRequest.notes());

        routineService.save(routine);

        return "Ejercicio actualizado correctamente.";
    }

    @Override
    @Transactional
    public String deleteExerciseFromRoutine(int routineId, int exerciseId) {
        Routine routine = routineService.findRoutineById(routineId);

        Set<RoutineEntry> exercises = routine.getExercises();

        RoutineEntry exercise = exercises.stream()
                .filter(e -> e.getExercise().getId().equals(exerciseId))
                .findFirst().orElseThrow(() -> {
                    logger.error("No se encontró el ejercicio con id {} en la rutina con id {}", exerciseId, routineId);
                    return new GlobalException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado en la rutina");
                });

        exercises.remove(exercise);
        routineService.save(routine);

        return "Ejercicio eliminado de la rutina correctamente";
    }
}

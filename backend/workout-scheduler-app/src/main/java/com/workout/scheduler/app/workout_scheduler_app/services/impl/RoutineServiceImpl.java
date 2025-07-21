package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.mappers.RoutineMapper;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import com.workout.scheduler.app.workout_scheduler_app.repositories.RoutineRepository;
import com.workout.scheduler.app.workout_scheduler_app.security.SecurityContextHelper;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineEntryService;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineService;
import com.workout.scheduler.app.workout_scheduler_app.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@Slf4j
public class RoutineServiceImpl implements RoutineService {

    @Autowired private SecurityContextHelper securityContextHelper;
    @Autowired private UserService userService;
    @Lazy @Autowired private RoutineEntryService routineEntryService;
    @Autowired private  RoutineRepository routineRepository;
    @Autowired private RoutineMapper routineMapper;
    private static final Logger logger = LoggerFactory.getLogger(RoutineServiceImpl.class);

    @Override
    public void save(Routine routine) {
        routineRepository.save(routine);
    }

    @Override
    public boolean routineExistsById(int id) {
        return routineRepository.existsByIdAndEnabledTrue(id);
    }

    @Override
    public Routine findRoutineById(int id) {
        return routineRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> {
                    logger.error("Rutina con id {} no encontrada", id);
                    return new GlobalException(HttpStatus.NOT_FOUND, "Rutina con id " + id + " no encontrada");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoutineDTO> getUserRoutines(int userId) {
        if (!userService.existsById(userId)) {
            logger.error("Usuario con id {} no encontrado", userId);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Usuario con id " + userId + " no encontrado");
        }

        return routineMapper.routineDTOListFromRoutineList(routineRepository.getUserRoutines(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineDTO getRoutineById(int id) {
        return routineMapper.routineDTOFromRoutine(routineRepository.getRoutineById(id)
                .orElseThrow(() -> {
                    logger.error("Rutina con id {} no encontrada", id);
                    return new GlobalException(HttpStatus.NOT_FOUND, "Rutina con id " + id + " no encontrada");
                }));
    }

    @Override
    @Transactional
    public String createRoutine(NewRoutineDTO data) {
        var routine = new Routine();

        routine.setName(data.name());
        routine.setExercises(routineEntryService.createRoutineEntries(routine, data.exercises()));
        routine.setUser(userService.getUserById(securityContextHelper.getCurrentUserId()));

        routineRepository.save(routine);

        return "Done";
    }

    @Override
    @Transactional
    public String changeRoutineName(int id, String newName) {
        if(!routineRepository.existsByIdAndEnabledTrue(id)) {
            logger.error("Rutina con id {} no encontrada", id);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Rutina con id " + id + " no encontrada");
        }

        routineRepository.changeRoutineName(id, newName);

        return "Nombre de rutina cambiado correctamente.";
    }

    @Override
    @Transactional
    public String deleteRoutine(int id) {
        routineRepository.setRoutineAsInactive(id);
        return "Rutina eliminada correctamente.";
    }
}
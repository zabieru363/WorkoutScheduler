package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.enums.ERoutineListType;
import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.mappers.RoutineMapper;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.SavedRoutine;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;
import com.workout.scheduler.app.workout_scheduler_app.security.SecurityContextHelper;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineService;
import com.workout.scheduler.app.workout_scheduler_app.services.SavedRoutineService;
import com.workout.scheduler.app.workout_scheduler_app.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedRoutineServiceImpl implements SavedRoutineService {

    private final SecurityContextHelper securityContextHelper;
    private final UserService userService;
    private final RoutineService routineService;
    private final RoutineMapper routineMapper;
    private static final Logger logger = LoggerFactory.getLogger(SavedRoutineServiceImpl.class);

    /**
     * Comprueba si un usuario ya ha guardado una rutina.
     * @param routineId El id de la rutina a comprobar.
     * @param savedRoutines El conjunto de rutinas guardadas por el usuario.
     * @return true si la rutina ya está guardada por el usuario, false en caso contrario.
     */
    public boolean isRoutineAlreadySavedByUser(int routineId, Set<SavedRoutine> savedRoutines, ERoutineListType listType) {
        return savedRoutines.stream().anyMatch(item ->
                item.getRoutine().getId().equals(routineId) && item.getListType().equals(listType));
    }

    /**
     * Comprueba si el usuario logueado es el creador de la rutina.
     * @param currentUserId El id del usuario logueado.
     * @param creatorRoutineId El id de la rutina a comprobar.
     * @return true si el usuario logueado es el creador de la rutina, false en caso contrario.
     */
    public boolean isLoggedUserEqualsThanRoutineCreator(Integer currentUserId, Integer creatorRoutineId) {
        return currentUserId.equals(creatorRoutineId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoutineDTO> getAllRoutinesOfList(String listType) {
        User user = userService.getUserById(securityContextHelper.getCurrentUserId());
        ERoutineListType type;

        try {
            type = ERoutineListType.valueOf(listType);
        }catch (IllegalArgumentException ex) {
            logger.error("Esa lista de rutinas no existe: {}", listType);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Lista de rutinas no válida");
        }

        return routineMapper.routineDTOListFromRoutineList(user.getSavedRoutines().stream()
                .filter(item -> item.getListType().equals(type))
                .map(SavedRoutine::getRoutine)
                .collect(Collectors.toSet()));
    }

    private SavedRoutine createSavedRoutineEntity(ERoutineListType type, Routine routine, User user) {
        var savedRoutine = new SavedRoutine();

        savedRoutine.setRoutine(routine);
        savedRoutine.setUser(user);
        savedRoutine.setListType(type);

        return savedRoutine;
    }

    @Override
    @Transactional
    public String saveRoutineInList(int routineId, String listType) {
        int currentUserId = securityContextHelper.getCurrentUserId();

        User user = userService.getUserById(currentUserId);
        Routine routine = routineService.findRoutineById(routineId);

        ERoutineListType type;

        try {
            type = ERoutineListType.valueOf(listType);
        }catch (IllegalArgumentException ex) {
            logger.error("Esa lista de rutinas no existe: {}", listType);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Lista de rutinas no válida");
        }

        Set<SavedRoutine> savedRoutines = user.getSavedRoutines();

        if(type.equals(ERoutineListType.SAVED) && isLoggedUserEqualsThanRoutineCreator(currentUserId, routine.getUser().getId())) {
            logger.error("El usuario con ID {} no puede guardar su propia rutina con ID {}", currentUserId, routineId);
            throw new GlobalException(HttpStatus.CONFLICT, "Un usuario no puede guardar su propia rutina");
        }

        if(isRoutineAlreadySavedByUser(routineId, savedRoutines, type)) {
            logger.error("La rutina con ID {} ya está guardada para el usuario con ID {} en esta lista", routineId, currentUserId);
            throw new GlobalException(HttpStatus.CONFLICT, "Esta rutina ya está guardada para este usuario en esta lista");
        }

        savedRoutines.add(createSavedRoutineEntity(type, routine, user));
        userService.save(user);

        return "Rutina guardada correctamente para el usuario con ID " + currentUserId;
    }

    @Override
    @Transactional
    public String unsaveRoutineInList(int routineId, String listType) {
        int currentUserId = securityContextHelper.getCurrentUserId();

        User user = userService.getUserById(securityContextHelper.getCurrentUserId());

        ERoutineListType type;

        try {
            type = ERoutineListType.valueOf(listType);
        }catch (IllegalArgumentException ex) {
            logger.error("Esa lista de rutinas no existe: {}", listType);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Lista de rutinas no válida");
        }

        if(!routineService.routineExistsById(routineId)) {
            logger.error("Rutina con ID {} no encontrada", routineId);
            throw new GlobalException(HttpStatus.NOT_FOUND, "Rutina no encontrada");
        }

        Set<SavedRoutine> savedRoutines = user.getSavedRoutines();

        if(!isRoutineAlreadySavedByUser(routineId, savedRoutines, type)) {
            logger.error("La rutina con ID {} no está guardada para el usuario con ID {}", routineId, currentUserId);
            throw new GlobalException(HttpStatus.CONFLICT, "El usuario no tiene esta rutina guardada");
        }

        savedRoutines.removeIf(item ->
                item.getRoutine().getId().equals(routineId) && item.getListType().equals(type));

        userService.save(user);

        return "Rutina eliminada correctamente para el usuario con ID " + currentUserId;
    }
}
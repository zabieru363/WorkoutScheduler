package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.RoutineRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.Set;

public interface RoutineRatingRepository extends JpaRepository<RoutineRating, Integer> {
    @Query(value = "SELECT new com.workout.scheduler.app.workout_scheduler_app" +
            ".models.dtos.RoutineRatingDTO(" +
            "rr.id, rr.stars, rr.comment, rr.routine.id, rr.createdBy, rr.modifiedAt) " +
            "FROM RoutineRating rr  " +
            "WHERE rr.routine.id = ?1 AND rr.enabled = true")
    Set<RoutineRatingDTO> getAllRatingsOfRoutine(int routineId);
    boolean existsByIdAndEnabledTrue(int id);
    boolean existsByCreatedByAndEnabledTrue(int id);
    Optional<RoutineRating> findByIdAndEnabledTrue(int id);
}
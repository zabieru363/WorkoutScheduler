package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Integer> {

    boolean existsByIdAndEnabledTrue(int id);

    @Query(value = "SELECT r FROM Routine r " +
            "LEFT JOIN FETCH r.exercises " +
            "WHERE r.user.id = ?1 AND r.enabled = true")
    Set<Routine> getUserRoutines(int userId);

    @Query(value = "SELECT r FROM Routine r " +
            "LEFT JOIN FETCH r.exercises " +
            "WHERE r.id = ?1 AND r.enabled = true")
    Optional<Routine> getRoutineById(int id);

    @Modifying
    @Query(value = "UPDATE Routine r SET r.name = ?2 WHERE r.id = ?1")
    void changeRoutineName(int id, String newName);

    @Modifying
    @Query(value = "UPDATE Routine r SET r.enabled = false WHERE r.id = ?1")
    void setRoutineAsInactive(int routineId);

    Optional<Routine> findByIdAndEnabledTrue(int id);
}
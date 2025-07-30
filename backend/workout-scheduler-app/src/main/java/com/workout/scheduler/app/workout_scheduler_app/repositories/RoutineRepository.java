package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Integer>, JpaSpecificationExecutor<Routine> {

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

    @Query(value = "SELECT r.id " +
            "FROM routines r " +
            "LEFT JOIN routines_ratings rr ON rr.routine_id = r.id " +
            "LEFT JOIN user_routine_lists sr ON sr.routine_id = r.id AND sr.list_type = 'LIKED' " +
            "WHERE r.id IN (:ids) " +
            "GROUP BY r.id " +
            "ORDER BY (COUNT(DISTINCT rr.id) + COUNT(DISTINCT sr.id)) DESC", nativeQuery = true)
    List<Integer> filterRoutinesByPopularity(@Param(value = "ids") Set<Integer> routinesIds);
}
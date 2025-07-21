package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.models.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    @Query(value = "SELECT e FROM Exercise e " +
            "LEFT JOIN FETCH e.images WHERE LOWER(e.name) LIKE %:name% " +
            "AND e.enabled = true " +
            "ORDER BY e.name")
    List<Exercise> findExercisesByName(String name);

    Set<Exercise> findByIdInAndEnabledTrue(Set<Integer> ids);

    @Query(value = "SELECT e FROM Exercise e " +
            "LEFT JOIN FETCH e.images " +
            "WHERE e.id = :id AND e.enabled = true")
    Optional<Exercise> getExerciseById(int id);

    boolean existsByIdAndEnabledTrue(Integer id);

    @Modifying
    @Query(value = "UPDATE Exercise e SET e.enabled = false WHERE e.id = :id")
    void deleteCustomExercise(Integer id);
}
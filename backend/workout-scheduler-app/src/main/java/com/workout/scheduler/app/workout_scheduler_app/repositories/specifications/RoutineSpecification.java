package com.workout.scheduler.app.workout_scheduler_app.repositories.specifications;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineFiltersDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Exercise;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Routine;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.RoutineEntry;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RoutineSpecification {

    private RoutineSpecification() {}

    private static Predicate applyFilters(RoutineFiltersDTO filters, Root<Routine> root, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        // * Filtro por nombre
        if (StringUtils.isNotEmpty(filters.name()))
            predicate = cb.and(predicate, containsName(root, cb, filters.name()));

        // * Filtro por músculo principal
        if (StringUtils.isNotEmpty(filters.mainMuscle()))
            predicate = cb.and(predicate, hasMainMuscle(root, cb, filters.mainMuscle()));

        // * Filtro por músculo secundario
        if (StringUtils.isNotEmpty(filters.secondaryMuscle()))
            predicate = cb.and(predicate, hasSecondaryMuscle(root, cb, filters.secondaryMuscle()));

        // * Filtro por antes de x fecha
        if (filters.before() != null)
            predicate = cb.and(predicate, matchesWithDate(root, cb, new LocalDateTime[]{filters.before()}, "before"));

        // * Filtro por después de x fecha
        if (filters.after() != null)
            predicate = cb.and(predicate, matchesWithDate(root, cb, new LocalDateTime[]{filters.after()}, "after"));

        // * Filtro de x fecha a x fecha
        if (filters.dates() != null)
            predicate = cb.and(predicate, matchesWithDate(root, cb, filters.dates(), "between"));

        // * Filtro para ver si contiene x ejercicios
        if (filters.exercises() != null && !filters.exercises().isEmpty())
            predicate = cb.and(predicate, hasExerciseNameIn(root, cb, filters.exercises().stream()
                    .map(String::toLowerCase).collect(Collectors.toSet())));

        return predicate;
    }

    public static Specification<Routine> searchRoutinesByFilters(RoutineFiltersDTO filters) {
        return (root, query, cb) -> applyFilters(filters, root, cb);
    }

    private static Predicate containsName(Root<Routine> root, CriteriaBuilder cb, String name) {
        return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Predicate matchesWithDate(Root<Routine> root, CriteriaBuilder cb, LocalDateTime[] dates, String dateFilter) {
        Map<String, Supplier<Predicate>> predicatesMap = Map.of(
                "before", () -> cb.lessThan(root.get("createdAt"), dates[0]),
                "after", () -> cb.greaterThan(root.get("createdAt"), dates[0]),
                "between", () -> cb.between(root.get("createdAt"), dates[0], dates[1])
        );

        return predicatesMap.get(dateFilter).get();
    }

    private static Predicate hasMainMuscle(Root<Routine> root, CriteriaBuilder cb, String mainMuscle) {
        Join<Routine, RoutineEntry> entryJoin = root.join("exercises");
        Join<RoutineEntry, Exercise> exerciseJoin = entryJoin.join("exercise");

        return cb.equal(
                cb.lower(exerciseJoin.get("mainMuscle")),
                mainMuscle.toLowerCase());
    }

    private static Predicate hasSecondaryMuscle(Root<Routine> root, CriteriaBuilder cb, String secondaryMuscle) {
        Join<Routine, RoutineEntry> entryJoin = root.join("exercises");
        Join<RoutineEntry, Exercise> exerciseJoin = entryJoin.join("exercise");

        return cb.equal(
                cb.lower(exerciseJoin.get("secondaryMuscle")),
                secondaryMuscle.toLowerCase());
    }

    private static Predicate hasExerciseNameIn(Root<Routine> root, CriteriaBuilder cb, Set<String> exerciseNames) {
        Join<Routine, RoutineEntry> entryJoin = root.join("exercises");
        Join<RoutineEntry, Exercise> exerciseJoin = entryJoin.join("exercise");

        return cb.lower(exerciseJoin.get("name")).in(exerciseNames);
    }

}

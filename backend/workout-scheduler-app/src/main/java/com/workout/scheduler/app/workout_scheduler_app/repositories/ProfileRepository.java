package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.models.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
}
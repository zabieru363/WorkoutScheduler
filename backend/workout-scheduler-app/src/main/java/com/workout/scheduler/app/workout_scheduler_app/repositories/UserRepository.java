package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.UserDataDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile " +
            "LEFT JOIN FETCH u.roles WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmailWithProfileAndRoles(@Param("username") String username, @Param("email") String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);

    @Query(value = "SELECT new com.workout.scheduler.app.workout_scheduler_app" +
            ".models.dtos.UserDataDTO(" +
            "u.id, u.username, u.email, u.enabled, u.createdAt," +
            "p.name, p.lastname, p.phone, p.height, p.weight," +
            "p.personType, p.trainings, p.birthdate) " +
            "FROM User u LEFT JOIN u.profile p " +
            "WHERE u.id = ?1")
    Optional<UserDataDTO> getUserDataByUserId(int id);
}
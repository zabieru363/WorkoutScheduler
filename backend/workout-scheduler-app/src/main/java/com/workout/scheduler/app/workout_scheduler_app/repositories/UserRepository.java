package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.UserDataDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByIdAndEnabledFalse(int id);
    Optional<User> findByIdAndEnabledTrue(int id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile " +
            "LEFT JOIN FETCH u.roles WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmailWithProfileAndRoles(@Param("username") String username, @Param("email") String email);
    boolean existsByIdAndEnabledTrue(int id);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByIdAndEnabledFalse(int id);

    @Modifying
    @Query(value = "UPDATE User u SET u.enabled = true WHERE u.id = ?1")
    void setUserAsActive(int userId);

    @Query(value = "SELECT new com.workout.scheduler.app.workout_scheduler_app" +
            ".models.dtos.UserDataDTO(" +
            "u.id, u.username, u.email, u.createdAt," +
            "p.name, p.lastname, p.phone, p.height, p.weight," +
            "p.personType, p.trainings, p.birthdate) " +
            "FROM User u LEFT JOIN u.profile p " +
            "WHERE u.id = ?1 AND u.enabled = true")
    Optional<UserDataDTO> getUserDataByUserId(int id);
}
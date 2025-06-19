package com.workout.scheduler.app.workout_scheduler_app.repositories;

import com.workout.scheduler.app.workout_scheduler_app.enums.EConfirmationCodeStatus;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Integer> {
    Boolean existsByUserIdAndStatusAndCodeAndExpiresAtAfter(int userId, EConfirmationCodeStatus status, int code, LocalDateTime target);
    @Modifying
    @Query(value = "DELETE FROM ConfirmationCode cc WHERE cc.user.id = :userId")
    void deleteAllUserConfirmationCodes(@Param("userId") int userId);
    @Modifying
    @Query(value = "UPDATE ConfirmationCode cc SET cc.status = :status WHERE cc.user.id = :userId")
    void updateUserConfirmationCode(@Param("status") EConfirmationCodeStatus status, @Param("userId") int userId);
}
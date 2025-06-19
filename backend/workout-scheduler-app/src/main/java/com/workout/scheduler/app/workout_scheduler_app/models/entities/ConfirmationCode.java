package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import com.workout.scheduler.app.workout_scheduler_app.enums.EConfirmationCodeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "confirmation_codes")
public class ConfirmationCode {
    // TODO: Crear tarea programada para borrar los códigos que ya se hayan pasado o estén deshabilitados.

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer code;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private EConfirmationCodeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
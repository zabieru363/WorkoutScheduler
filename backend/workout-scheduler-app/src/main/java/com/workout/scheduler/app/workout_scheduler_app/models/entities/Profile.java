package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import com.workout.scheduler.app.workout_scheduler_app.enums.EPersonType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "profiles")
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;
    private String lastname;
    private String phone;
    private Double height;
    private Double weight;

    // Tipo de persona (ectomorfo, mesomorfo, endomorfo...)
    @Column(name = "person_type")
    private EPersonType personType;

    // Cuantas veces entrena la persona por semana
    private byte trainings;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime birthdate;

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phone='" + phone + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", personType=" + personType +
                ", trainings=" + trainings +
                ", birthdate=" + birthdate +
                '}';
    }
}
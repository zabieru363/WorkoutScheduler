package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "exercises")
public class Exercise {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String name;
    private String mainMuscle;
    private String secondaryMuscle;
    private String description;
    private Boolean requireEquipment;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            orphanRemoval = true,
            mappedBy = "exercise")
    private Set<ExerciseResource> exerciseResources = new HashSet<>();

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "is_custom")
    private Boolean isCustom;

    private Boolean enabled;

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mainMuscle='" + mainMuscle + '\'' +
                ", secondaryMuscle='" + secondaryMuscle + '\'' +
                ", description='" + description + '\'' +
                ", requireEquipment=" + requireEquipment +
                ", addedAt=" + addedAt +
                ", isCustom=" + isCustom +
                ", enabled=" + enabled +
                '}';
    }
}
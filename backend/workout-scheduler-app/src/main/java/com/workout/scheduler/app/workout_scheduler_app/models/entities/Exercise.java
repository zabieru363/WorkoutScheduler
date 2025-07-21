package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "main_muscle", nullable = false)
    private String mainMuscle;
    @Column(name = "secondary_muscle")
    private String secondaryMuscle;
    private String description;
    @Column(name = "require_equipment")
    private Boolean requireEquipment;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "exercise",
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            orphanRemoval = true)
    private List<ExerciseImage> images = new ArrayList<>();

    @Column(name = "video_url")
    private String videoURL;

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "is_custom")
    private Boolean isCustom;

    private Boolean enabled = true;

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
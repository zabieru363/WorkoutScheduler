package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import jakarta.persistence.*;
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
@Table(name = "exercises_resources")
public class ExerciseResource {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "exerciseResource",
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            orphanRemoval = true)
    private List<ExerciseImage> images = new ArrayList<>();

    @Column(name = "video_url")
    private String videoURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;

    private Boolean enabled;

    @Override
    public String toString() {
        return "ExerciseResource{" +
                "id=" + id +
                ", videoURL='" + videoURL + '\'' +
                ", addedAt=" + addedAt +
                ", enabled=" + enabled +
                '}';
    }
}
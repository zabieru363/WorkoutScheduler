package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "exercises_image")
public class ExerciseImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_resource_id")
    private ExerciseResource exerciseResource;

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;

    private Boolean enabled;

}
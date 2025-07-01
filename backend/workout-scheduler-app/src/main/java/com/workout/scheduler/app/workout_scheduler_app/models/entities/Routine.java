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
@Table(name = "routines")
public class Routine {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String name;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            orphanRemoval = true,
            mappedBy = "routine")
    private Set<RoutineDay> makeOn = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            mappedBy = "routine")
    private Set<RoutineEntry> exercises = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Boolean enabled;

    @Override
    public String toString() {
        return "Routine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                '}';
    }
}
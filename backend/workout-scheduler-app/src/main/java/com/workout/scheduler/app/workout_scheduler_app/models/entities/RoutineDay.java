package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import com.workout.scheduler.app.workout_scheduler_app.enums.EDay;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "routines_days")
public class RoutineDay {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EDay day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;
}
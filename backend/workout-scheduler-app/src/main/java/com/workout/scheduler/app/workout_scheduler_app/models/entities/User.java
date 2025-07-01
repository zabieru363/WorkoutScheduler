package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;

    @OneToOne(
            mappedBy = "user",
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            orphanRemoval = true)
    private Profile profile;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            orphanRemoval = true,
            mappedBy = "user")
    private Set<ConfirmationCode> codes = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            mappedBy = "user")
    private Set<Routine> routines = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Boolean enabled;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                '}';
    }
}
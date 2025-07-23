package com.workout.scheduler.app.workout_scheduler_app.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user")
    private Set<SavedRoutine> savedRoutines = new HashSet<>();

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
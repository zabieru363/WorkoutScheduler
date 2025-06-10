package com.workout.scheduler.app.workout_scheduler_app.security;

import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Esto es una clase que representa los detalles del usuario.
 * Contiene solo necesario para que Spring Security pueda
 * autenticar a un usuario.
 */
@AllArgsConstructor
@Getter
public class AuthenticatedUser implements UserDetails {

    private Integer id;
    private String username;
    private String password;
    private Boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    public static AuthenticatedUser create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new AuthenticatedUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                authorities
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}

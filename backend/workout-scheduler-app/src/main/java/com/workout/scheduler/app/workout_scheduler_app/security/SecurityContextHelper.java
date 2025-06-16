package com.workout.scheduler.app.workout_scheduler_app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Clase que trabaja con el SecurityContextHolder de spring
 * security y proporciona métodos útiles para obtener los
 * datos del usuario logueado o su id.
 */
@Component
public class SecurityContextHelper {

    public AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) return null;

        return (AuthenticatedUser) authentication.getPrincipal();
    }

    public Integer getCurrentUserId() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

}

package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;
import com.workout.scheduler.app.workout_scheduler_app.repositories.UserRepository;
import com.workout.scheduler.app.workout_scheduler_app.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Esta clase le dice a Spring Security donde están nuestros usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Recupera los detalles de un usuario por id.
     * @param id El id del usuario buscado
     * @return El usuario encontrado
     * @throws UsernameNotFoundException Este error se lanzará cuando el
     * usuario no sea encontrado.
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id.intValue())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con id: " + id));

        return AuthenticatedUser.create(user);
    }

    /**
     * Recupera los detalles de un usuario de la base de datos.
     * @param value Puede ser o el username o el email
     * @return El usuario encontrado
     * @throws UsernameNotFoundException Este error se lanzará cuando el
     * usuario no sea encontrado.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String value) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmailWithProfileAndRoles(value, value)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con username o email: " + value));

        return AuthenticatedUser.create(user);
    }
}
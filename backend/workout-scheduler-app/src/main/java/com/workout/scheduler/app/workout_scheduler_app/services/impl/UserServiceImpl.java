package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.enums.EPersonType;
import com.workout.scheduler.app.workout_scheduler_app.enums.ERole;
import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Profile;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;
import com.workout.scheduler.app.workout_scheduler_app.repositories.RoleRepository;
import com.workout.scheduler.app.workout_scheduler_app.repositories.UserRepository;
import com.workout.scheduler.app.workout_scheduler_app.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public boolean existsByUsernameOrEmail(String property, String value) {
        return property.equals("username") ?
                userRepository.existsByUsernameIgnoreCase(value) :
                userRepository.existsByEmailIgnoreCase(value);
    }

    @Override
    public String createUser(NewUserDTO data) {
        if(existsByUsernameOrEmail("username", data.getUsername())) {
            logger.error("Este nombre de usuario ya existe");
            throw new GlobalException(HttpStatus.CONFLICT, "Este nombre de usuario ya existe");
        }

        if(existsByUsernameOrEmail("email", data.getEmail())) {
            logger.error("Este email ya existe");
            throw new GlobalException(HttpStatus.CONFLICT, "Este email ya existe");
        }

        Profile profile = new Profile();

        profile.setName(data.getName());
        profile.setLastname(data.getLastname());
        profile.setPhone(data.getPhone());
        profile.setHeight(data.getHeight());
        profile.setWeight(data.getWeight());
        profile.setPersonType(EPersonType.valueOf(data.getPersonType()));
        profile.setBirthdate(data.getBirthdate());
        profile.setTrainings(data.getTrainings());

        User user = new User();

        user.setUsername(data.getUsername());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        user.setEmail(data.getEmail());
        user.setProfile(profile);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(Set.of(roleRepository.findByName(ERole.ROLE_USER)));

        profile.setUser(user);

        userRepository.save(user);

        return "Done";
    }
}
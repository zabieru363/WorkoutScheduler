package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.enums.EConfirmationCodeStatus;
import com.workout.scheduler.app.workout_scheduler_app.enums.EPersonType;
import com.workout.scheduler.app.workout_scheduler_app.enums.ERole;
import com.workout.scheduler.app.workout_scheduler_app.exceptions.GlobalException;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewEmailDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.UserDataDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.ConfirmationCode;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.Profile;
import com.workout.scheduler.app.workout_scheduler_app.models.entities.User;
import com.workout.scheduler.app.workout_scheduler_app.repositories.ConfirmationCodeRepository;
import com.workout.scheduler.app.workout_scheduler_app.repositories.RoleRepository;
import com.workout.scheduler.app.workout_scheduler_app.repositories.UserRepository;
import com.workout.scheduler.app.workout_scheduler_app.security.SecurityContextHelper;
import com.workout.scheduler.app.workout_scheduler_app.services.EmailService;
import com.workout.scheduler.app.workout_scheduler_app.services.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.workout.scheduler.app.workout_scheduler_app.utils.EmailTexts.CONFIRMATION_CODE_EMAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final SecurityContextHelper securityContextHelper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private void validateUsernameAndEmail(String username, String email) {
        if(existsByUsernameOrEmail("username", username)) {
            logger.error("Este nombre de usuario ya existe");
            throw new GlobalException(HttpStatus.CONFLICT, "Este nombre de usuario ya existe");
        }

        if(existsByUsernameOrEmail("email", email)) {
            logger.error("Este email ya existe");
            throw new GlobalException(HttpStatus.CONFLICT, "Este email ya existe");
        }
    }

    private Profile createUserData(NewUserDTO data) {
        Profile profile = new Profile();

        profile.setName(data.getName());
        profile.setLastname(data.getLastname());
        profile.setPhone(data.getPhone());
        profile.setHeight(data.getHeight());
        profile.setWeight(data.getWeight());
        profile.setPersonType(EPersonType.valueOf(data.getPersonType()));
        profile.setBirthdate(data.getBirthdate());
        profile.setTrainings(data.getTrainings());

        return profile;
    }

    private ConfirmationCode createNewConfirmationCode(User user) {
        ConfirmationCode confirmationCode = new ConfirmationCode();

        Random random = new Random();
        int code = random.nextInt(10000, 99999);

        confirmationCode.setCode(code);
        confirmationCode.setCreatedAt(LocalDateTime.now());
        confirmationCode.setExpiresAt(LocalDateTime.now().plusHours(2));
        confirmationCode.setStatus(EConfirmationCodeStatus.NEW);
        confirmationCode.setUser(user);

        return confirmationCode;
    }

    public boolean existsByUsernameOrEmail(String property, String value) {
        return property.equals("username") ?
                userRepository.existsByUsernameIgnoreCase(value) :
                userRepository.existsByEmailIgnoreCase(value);
    }


    @Override
    @Transactional
    public String preRegister(NewUserDTO data) {
        validateUsernameAndEmail(data.getUsername(), data.getEmail());

        Profile profile = createUserData(data);
        User user = new User();

        user.setUsername(data.getUsername());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        user.setEmail(data.getEmail());
        user.setProfile(profile);
        user.setEnabled(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(Set.of(roleRepository.findByName(ERole.ROLE_USER)));

        profile.setUser(user);

        ConfirmationCode confirmationCode = createNewConfirmationCode(user);
        user.getCodes().add(confirmationCode);

        try {
            emailService.sendConfirmationCodeEmail(new NewEmailDTO(
                    user.getEmail(),
                    "Confirmación de registro",
                    CONFIRMATION_CODE_EMAIL,
                    Map.of(
                            "username", user.getUsername(),
                            "code", confirmationCode.getCode()
                    )
            ));
        }catch (MessagingException ex) {
            throw new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, "Algo salió mal al enviar el correo.");
        }

        userRepository.save(user);

        return "Done";
    }

    @Override
    @Transactional
    public String registerConfirmation(int userId, String attempt) {
        if(!userRepository.existsByIdAndEnabledFalse(userId)) {
            logger.error("Usuario no encontrado");
            throw new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        int code;

        try {
            code = Integer.parseInt(attempt);
        }catch (NumberFormatException ex) {
            logger.error("Lo que ha recibido el servicio no es un código");
            throw new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, "Lo que ha recibido el servicio no es un código");
        }

        if(Boolean.FALSE.equals(confirmationCodeRepository
                .existsByUserIdAndStatusAndCodeAndExpiresAtAfter
                        (userId, EConfirmationCodeStatus.NEW, code, LocalDateTime.now()))) {
            logger.error("El código no fue encontrado o expiró");
            throw new GlobalException(HttpStatus.NOT_FOUND, "El código no fue encontrado o expiró");
        }

        userRepository.setUserAsActive(userId);
        confirmationCodeRepository.updateUserConfirmationCode(EConfirmationCodeStatus.USED, userId);

        return "Registration completed";
    }

    @Override
    @Transactional
    public String resendConfirmationCode(int userId) {
        User user = userRepository.findByIdAndEnabledFalse(userId)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado");
                    return new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
                });

        confirmationCodeRepository.deleteAllUserConfirmationCodes(userId);

        ConfirmationCode confirmationCode = new ConfirmationCode();

        Random random = new Random();
        int code = random.nextInt(10000, 99999);

        confirmationCode.setCode(code);
        confirmationCode.setCreatedAt(LocalDateTime.now());
        confirmationCode.setExpiresAt(LocalDateTime.now().plusHours(2));
        confirmationCode.setStatus(EConfirmationCodeStatus.NEW);
        confirmationCode.setUser(user);

        try {
            emailService.sendConfirmationCodeEmail(new NewEmailDTO(
                    user.getEmail(),
                    "Confirmación de registro",
                    CONFIRMATION_CODE_EMAIL,
                    Map.of(
                            "username", user.getUsername(),
                            "code", confirmationCode.getCode()
                    )
            ));
        }catch (MessagingException ex) {
            throw new GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, "Algo salió mal al enviar el correo.");
        }

        confirmationCodeRepository.save(confirmationCode);

        return "New confirmation code has been created";
    }

    @Override
    @Transactional(readOnly = true)
    public UserDataDTO getUserData() {
        return userRepository.getUserDataByUserId(securityContextHelper.getCurrentUserId())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado");
                    return new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
                });
    }
}
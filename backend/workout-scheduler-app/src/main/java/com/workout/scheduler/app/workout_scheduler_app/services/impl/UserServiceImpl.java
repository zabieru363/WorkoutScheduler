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
        var profile = new Profile();

        profile.setName(data.name());
        profile.setLastname(data.lastname());
        profile.setPhone(data.phone());
        profile.setHeight(data.height());
        profile.setWeight(data.weight());
        profile.setPersonType(EPersonType.valueOf(data.personType()));
        profile.setBirthdate(data.birthdate());
        profile.setTrainings(data.trainings());

        return profile;
    }

    private ConfirmationCode createNewConfirmationCode(User user) {
        ConfirmationCode confirmationCode = new ConfirmationCode();

        var random = new Random();
        int code = random.nextInt(10000, 99999);

        confirmationCode.setCode(code);
        confirmationCode.setCreatedAt(LocalDateTime.now());
        confirmationCode.setExpiresAt(LocalDateTime.now().plusHours(2));
        confirmationCode.setStatus(EConfirmationCodeStatus.NEW);
        confirmationCode.setUser(user);

        return confirmationCode;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsById(int id) {
        return userRepository.existsByIdAndEnabledTrue(id);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado");
                    return new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
                });
    }

    @Override
    public boolean existsByUsernameOrEmail(String property, String value) {
        return property.equals("username") ?
                userRepository.existsByUsernameIgnoreCase(value) :
                userRepository.existsByEmailIgnoreCase(value);
    }

    @Override
    @Transactional
    public String preRegister(NewUserDTO data) {
        validateUsernameAndEmail(data.username(), data.email());

        Profile profile = createUserData(data);
        User user = new User();

        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setEmail(data.email());
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

        return "Pre-registro completado.";
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

        return "Registro completado.";
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

        var random = new Random();
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

        return "Nuevo código de confirmación enviado correctamente";
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
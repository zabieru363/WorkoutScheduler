package com.workout.scheduler.app.workout_scheduler_app.controllers;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.ErrorResponse;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.UserDataDTO;
import com.workout.scheduler.app.workout_scheduler_app.services.SavedRoutineService;
import com.workout.scheduler.app.workout_scheduler_app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Operaciones relacionadas con la gestión de usuarios")
public class UserController {

    private final UserService userService;
    private final SavedRoutineService savedRoutineService;

    @PostMapping(value = "/pre-register")
    @Operation(
            summary = "Pre-registro",
            description = "Crea un nuevo usuario con el ROLE_USER y los datos para " +
                    "su perfil correspondiente y deja el usuario sin confirmar (enabled = false)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pre-registro completado",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Este nombre de usuario ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Este email ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<String> preRegister(@Valid @RequestBody NewUserDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.preRegister(data));
    }

    @PatchMapping(value = "/{userId}/register-confirmation")
    @Operation(
            summary = "Confirmación de registro",
            description = "Activa un usuario si el código coincide con el que envió el usuario y " +
                    "si este no ha expirado. El usuario debe de existir para poder confirmarlo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro completado",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lo que recibió el servicio no es un código",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<String> registerConfirmation(@PathVariable Integer userId, @RequestParam String attempt) {
        return ResponseEntity.ok(userService.registerConfirmation(userId, attempt));
    }

    @PatchMapping(value = "/{userId}/resend-confirmation-code")
    @Operation(
            summary = "Reenvío de código de confirmación",
            description = "Crea un nuevo código de confirmación y se lo manda al usuario. Elimina " +
                    "los que ya tenía anteriormente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nuevo código de confirmación enviado correctamente",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Algo salió mal al enviar el correo.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<String> resendConfirmationCode(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.resendConfirmationCode(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Obtener datos de usuario",
            description = "Obtiene los datos del usuario con la sesión iniciada. No hay " +
                    "que pasarle nada, ya que coge el id del usuario de la sesión. Devuelve " +
                    "datos generales + datos del perfil."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = UserDataDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<UserDataDTO> getUserData() {
        return ResponseEntity.ok(userService.getUserData());
    }

    // ! A estos endpoints no se les pasa el userId ya que los guardados son privados para cada usuario

    @GetMapping(value = "/saved-routines")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Obtener rutinas de una lista de un usuario",
            description = "Devuelve una lista de rutinas de una de las listas del usuario con la sesión iniciada. " +
                    "Se pueden obtener las rutinas guardadas, las favoritas o las creadas especificando el parámetro listType"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoutineDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lista no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Set<RoutineDTO>> getAllSavedRoutinesOfUser(@RequestParam String listType) {
        return ResponseEntity.ok(savedRoutineService.getAllRoutinesOfList(listType));
    }

    @PostMapping(value = "/saved-routines/{routineId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Guardar rutina en una lista del usuario",
            description = "Guarda una rutina para el usuario con la sesión iniciada en uan de sus " +
                    "listas de rutinas. Se puede especificar la lista que se quiere con el " +
                    "parámetro listType. Si ya la tiene guardada o si trata de " +
                    "guardar su propia rutina, lanzará una excepción."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rutina guardada correctamente para el usuario con ID {userId}",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rutina no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lista no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El usuario ya tiene esta rutina guardada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Un usuario no puede guardar su propia rutina",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<String> saveRoutine(@PathVariable int routineId, @RequestBody String listType) {
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoutineService.saveRoutineInList(routineId, listType));
    }

    @DeleteMapping(value = "/saved-routines/{routineId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Eliminar rutina de lista de usuario",
            description = "Quita una rutina de una de la lista de rutinas del usuario con la sesión iniciada. " +
                    "Se puede especificar la lista que se quiere con el parámetro listType. " +
                    "Si no la tiene guardada lanzará una excepción."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rutina eliminada correctamente para el usuario con ID {userId}",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rutina no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lista no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El usuario no tiene esta rutina guardada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<String> unsaveRoutine(@PathVariable int routineId, @RequestBody String listType) {
        return ResponseEntity.ok(savedRoutineService.unsaveRoutineInList(routineId, listType));
    }
}
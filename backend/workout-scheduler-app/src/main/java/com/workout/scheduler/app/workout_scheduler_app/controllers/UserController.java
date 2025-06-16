package com.workout.scheduler.app.workout_scheduler_app.controllers;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.ErrorResponse;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewUserDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.UserDataDTO;
import com.workout.scheduler.app.workout_scheduler_app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Operations related to users management")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/register")
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario con el ROLE_USER y los datos para " +
                    "su perfil correspondiente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado con éxito",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Este nombre de usuario ya existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),@ApiResponse(
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
    public ResponseEntity<String> createUser(@Valid @RequestBody NewUserDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(data));
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

}
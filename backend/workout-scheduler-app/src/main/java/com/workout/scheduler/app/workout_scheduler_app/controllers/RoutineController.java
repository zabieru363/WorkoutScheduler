package com.workout.scheduler.app.workout_scheduler_app.controllers;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.ErrorResponse;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineFiltersDTO;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Set;

@RestController
@RequestMapping(value = "/routines")
@RequiredArgsConstructor
@Tag(name = "Routine Controller", description = "Operaciones relacionadas con los rutinas de entrenamiento")
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping(value = "/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Listado de rutinas con filtros",
            description = "Devuelve las rutinas que coinciden con los filtros proporcionados. Si " +
            "no hay ninguna rutina que coincida con los filtros devolverá una lista vacía."
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
                    responseCode = "400",
                    description = "El filtro between requiere dos fechas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Set<RoutineDTO>> getRoutinesByFilters(
            @RequestBody RoutineFiltersDTO filters) {
        return ResponseEntity.ok(routineService.searchRoutinesByFilters(filters));
    }

    @GetMapping(value = "/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Obtener rutinas de usuario",
            description = "Devuelve las rutinas que ha creado el usuario que tiene el id especificado. Si" +
                    "no se encuentra ninguna rutina, devuelve una lista vacía."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de rutinas obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoutineDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El usuario no existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Set<RoutineDTO>> getUserRoutines(@PathVariable int userId) {
        return ResponseEntity.ok(routineService.getUserRoutines(userId));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Encontrar rutina por ID",
            description = "Obtiene una rutina por su ID. Si la rutina no existe, " +
                    "devuelve un error 404."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoutineDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró la rutina con el ID proporcionado",
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
    public ResponseEntity<RoutineDTO> getRoutineById(@PathVariable int id) {
        return ResponseEntity.ok(routineService.getRoutineById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Crear rutina",
            description = "Crea una nueva rutina de entrenamiento para el usuario con la sesión iniciada. " +
                    "Requiere un objeto NewRoutineDTO con los detalles de la rutina."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rutina creada correctamente",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El nombre de la rutina no puede estar vacío",
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
    public ResponseEntity<String> createRoutine(@Valid @RequestBody NewRoutineDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routineService.createRoutine(data));
    }

    @PatchMapping(value = "/{id}/change-name")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Cambiar nombre de rutina",
            description = "Cambia el nombre de la rutina con el ID especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nombre de rutina cambiado correctamente.",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rutina no encontrada",
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
    public ResponseEntity<String> changeRoutineName(@PathVariable int id, @RequestParam String newName) {
        return ResponseEntity.ok(routineService.changeRoutineName(id, newName));
    }

    @PatchMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Eliminar rutina",
            description = "Hace un borrado lógico de la rutina con el ID especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rutina eliminada correctamente.",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rutina no encontrada",
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
    public ResponseEntity<String> deleteRoutine(@PathVariable int id) {
        return ResponseEntity.ok(routineService.deleteRoutine(id));
    }

}
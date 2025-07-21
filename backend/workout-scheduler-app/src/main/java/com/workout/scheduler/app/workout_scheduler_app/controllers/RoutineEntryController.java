package com.workout.scheduler.app.workout_scheduler_app.controllers;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.ErrorResponse;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineEntryDTO;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/routines/{routineId}/entries")
@RequiredArgsConstructor
@Tag(name = "Routine Entry Controller", description = "Operaciones relacionadas con los ejercicios de las rutinas de entrenamiento")
public class RoutineEntryController {

    private final RoutineEntryService routineEntryService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Añadir ejercicio a rutina",
            description = "Añade un ejercicio a la rutina con el ID especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Nuevo ejercicio añadido correctamente.",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación. Los datos proporcionados no son válidos.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El ejercicio ya fue añadido a la rutina.",
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
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<String> addExerciseToRoutine(@PathVariable int routineId, @RequestBody NewRoutineEntryDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routineEntryService.addExerciseToRoutine(routineId, data));
    }

    @PatchMapping(value = "/{exerciseId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Modificar ejercicio de rutina",
            description = "Modifica un ejercicio de la rutina con el ID especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ejercicio actualizado correctamente.",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación. Los datos proporcionados no son válidos.",
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
                    description = "Ejercicio no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El ejercicio ya fue añadido a la rutina.",
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
    public ResponseEntity<String> changeExerciseInRoutine(
            @PathVariable int routineId,
            @PathVariable int exerciseId,
            @RequestBody NewRoutineEntryDTO data) {
        return ResponseEntity.ok(routineEntryService.changeExerciseInRoutine(routineId, exerciseId, data));
    }

    @DeleteMapping(value = "/{exerciseId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Eliminar ejercicio de rutina",
            description = "Elimina un ejercicio de la rutina con el ID especificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ejercicio eliminado de la rutina correctamente.",
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
                    responseCode = "404",
                    description = "Ejercicio no encontrada",
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
    public ResponseEntity<String> deleteExerciseFromRoutine(@PathVariable int routineId, @PathVariable int exerciseId) {
        return ResponseEntity.ok(routineEntryService.deleteExerciseFromRoutine(routineId, exerciseId));
    }

}
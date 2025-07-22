package com.workout.scheduler.app.workout_scheduler_app.controllers;

import com.workout.scheduler.app.workout_scheduler_app.exceptions.ErrorResponse;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewRoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.models.dtos.RoutineRatingDTO;
import com.workout.scheduler.app.workout_scheduler_app.services.RoutineRatingService;
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
import org.springframework.web.bind.annotation.RestController;
import java.util.Set;

@RestController
@RequestMapping(value = "/routines/{routineId}/ratings")
@RequiredArgsConstructor
@Tag(name = "Routine Rating Controller", description = "Operaciones relacionadas con las valoraciones de las rutinas")
public class RoutineRatingController {

    private final RoutineRatingService routineRatingService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Obtener valoraciones de una rutina",
            description = "Devuelve una lista de valoraciones de una rutina especifica por su ID. Si " +
                    "no se encuentra ninguna valoración, devuelve una lista vacía."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoutineRatingDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró la rutina",
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
    public ResponseEntity<Set<RoutineRatingDTO>> getAllRatingsOfRoutine(@PathVariable int routineId) {
        return ResponseEntity.ok(routineRatingService.getAllRatingsOfRoutine(routineId));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Crear valoración para una rutina",
            description = "Crea una nueva valoración para una rutina especificada por su ID para " +
                    "el usuario autenticada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Valoración creada correctamente..",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación. Los datos proporcionados no son válidos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró la rutina",
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
    public ResponseEntity<String> createRoutineRating(
            @PathVariable int routineId,
            @Valid @RequestBody NewRoutineRatingDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routineRatingService.createRoutineRating(routineId, data));
    }

    @PatchMapping(value = "/{ratingId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Actualizar valoración para una rutina",
            description = "Actualiza una valoración de una rutina especificada por su ID. " +
                    "Solo el usuario que creó la valoración puede actualizarla."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Valoración eliminada correctamente..",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solo el creador de la rutina puede actualizar esta valoración",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró la rutina",
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
    public ResponseEntity<String> updateRoutineRating(
            @PathVariable int routineId,
            @PathVariable int ratingId,
            @RequestBody NewRoutineRatingDTO data) {
        return ResponseEntity.ok(routineRatingService.updateRoutineRating(routineId, ratingId, data));
    }

    @PatchMapping(value = "/delete/{ratingId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Eliminar valoración para una rutina",
            description = "Elimina una valoración de una rutina especificada por su ID. " +
                    "Solo el usuario que creó la valoración puede eliminarla."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Valoración eliminada correctamente..",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solo el creador de la rutina puede eliminar esta valoración.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró la rutina",
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
    public ResponseEntity<String> deleteRoutineRating(
            @PathVariable int routineId,
            @PathVariable int ratingId) {
        return ResponseEntity.ok(routineRatingService.deleteRoutineRating(routineId, ratingId));
    }
}
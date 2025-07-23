package com.workout.scheduler.app.workout_scheduler_app.controllers;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.ExerciseDTO;
import com.workout.scheduler.app.workout_scheduler_app.exceptions.ErrorResponse;
import com.workout.scheduler.app.workout_scheduler_app.services.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping(value = "/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise Controller", description = "Operaciones relacionadas con los ejercicios")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Obtener ejercicios por nombre",
            description = "Devuelve una lista de ejercicios que coinciden con el nombre proporcionado. Si" +
                    "no se encuentra ningún ejercicio, devuelve una lista vacía."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de ejercicios obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ExerciseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<ExerciseDTO>> getExercisesByName(@RequestParam String name) {
        return ResponseEntity.ok(exerciseService.findExercisesByName(name));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
            summary = "Encontrar ejercicio por ID",
            description = "Obtiene un ejercicio por su ID. Si el ejercicio no existe, " +
                    "devuelve un error 404."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el ejercicio con el ID proporcionado",
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
    public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable Integer id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear ejercicio personalizado",
            description = "Crea un nuevo ejercicio personalizado. " +
                    "Requiere datos en formato JSON y opcionalmente imágenes asociadas al ejercicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Done",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El nombre del ejercicio no puede estar vacío.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ejercicio debe de tener un músculo principal.",
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
    public ResponseEntity<String> createCustomExercise(
            @RequestParam("data") String data,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.createCustomExercise(data, images));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Actualizar ejercicio personalizado",
            description = "Actualiza un ejercicio personalizado existente. " +
                    "Requiere el ID del ejercicio, datos en formato JSON y opcionalmente imágenes asociadas al ejercicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Done",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el ejercicio con el ID proporcionado.",
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
    public ResponseEntity<String> updateCustomExercise(
            @PathVariable Integer id,
            @RequestParam("data") String data,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(exerciseService.updateCustomExercise(id, data, images));
    }

    @PatchMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Eliminar ejercicio personalizado",
            description = "Elimina (desactiva) un ejercicio personalizado existente. " +
                    "Requiere el ID del ejercicio para ello."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Done",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el ejercicio con el ID proporcionado.",
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
    public ResponseEntity<String> deleteCustomExercise(@PathVariable Integer id) {
        return ResponseEntity.ok(exerciseService.deleteCustomExercise(id));
    }

}
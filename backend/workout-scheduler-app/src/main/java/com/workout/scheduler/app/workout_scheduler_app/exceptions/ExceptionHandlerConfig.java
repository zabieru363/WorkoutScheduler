package com.workout.scheduler.app.workout_scheduler_app.exceptions;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Manejador de excepciones, springboot lo trata cómo un
 * controlador así que hay que excluirlo de la documentación
 * con @Hidden
 */
@Hidden
@RestControllerAdvice
public class ExceptionHandlerConfig {

    /**
     * Se encargará de mapear excepciones GlobalException a ErrorResponse.
     * Estas excepciones son las que se lanzan en tiempo de ejecución de un
     * servicio, por ejemplo cuando un usuario no se encontró o algo por el estilo.
     * @param ex El objeto GlobalException que se lanza
     * @param request Los datos de la solicitud que se hizo
     * @return Un objeto ErrorResponse que se mapeará a un json correspondiente.
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse(
                        ex.getStatus().value(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getRequestURI()));
    }

    /**
     * Este es para mostrar los errores de validación cuando se trabaja
     * con Spring Validator. Va mostrando los errores separados por comas
     * en la propiedad message.
     * @param ex La excepción que se lanzó
     * @param request Los datos de la solicitud que se hizo
     * @return Un objeto ErrorResponse que se mapeará a un json correspondiente.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // * Devuelve todos los mensajes de validación:
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        errorMessage,
                        LocalDateTime.now(),
                        request.getRequestURI()));
    }

    /**
     * Este es para cubrir excepciones del tipo NullPointerException.
     * @param ex La excepción que se lanzó
     * @param request Los datos de la solicitud que se hizo
     * @return Un objeto ErrorResponse que se mapeará a un json correspondiente.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error no se esperaba un valor nulo.",
                        LocalDateTime.now(),
                        request.getRequestURI()));
    }

    /**
     * Este es para cubrir excepciones del tipo JwtException.
     * Cuando el token ha expirado y otros errores relacionados con JWT.
     * @param ex La excepción que se lanzó
     * @param request Los datos de la solicitud que se hizo
     * @return Un objeto ErrorResponse que se mapeará a un json correspondiente.
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException (JwtException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getRequestURI()));
    }

    /**
     * Este es para cubrir excepciones del tipo AccessDeniedException.
     * Estas se lanzan cuando un usuario accede a un servicio para el
     * cual no tiene permisos.
     * @param ex La excepción que se lanzó
     * @param request Los datos de la solicitud que se hizo
     * @return Un objeto ErrorResponse que se mapeará a un json correspondiente.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getRequestURI()));
    }

    /**
     * Este es para cubrir excepciones del tipo BadCredentialsException. Ocurre
     * cuando no se encuentra ningún usuario con las credenciales proporcionadas.
     * @param ex La excepción que se lanzó
     * @param request Los datos de la solicitud que se hizo
     * @return Un objeto ErrorResponse que se mapeará a un json correspondiente.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                        "Credenciales incorrectas.",
                        LocalDateTime.now(),
                        request.getRequestURI()));
    }

    /**
     * Este es para tratar el resto de errores que pudiesen ocurrir, para
     * errores genéricos y otras situaciones en las que no se sepa que ha pasado
     * @param ex La excepción que se lanzó
     * @param request Los datos de la solicitud que se hizo
     * @return Un objeto ErrorResponse que se mapeará a un json correspondiente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error inesperado: " + ex.getMessage() + ".",
                        LocalDateTime.now(),
                        request.getRequestURI()));
    }

}

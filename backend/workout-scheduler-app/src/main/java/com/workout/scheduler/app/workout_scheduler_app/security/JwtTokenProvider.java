package com.workout.scheduler.app.workout_scheduler_app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Esto es una clase para generar y validar tokens JWT
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * Permite firmar un token JWT con la llave secreta del application.properties
     * @return La llave secreta para firmar el token
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token jwt para el usuario autenticado.
     * @param authentication Objeto authentication de donde se sacarán
     * todos los datos necesarios del usuario.
     * @return El token jwt generado.
     */
    public String generateToken(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(Long.toString(authenticatedUser.getId()))
                .claim("username", authenticatedUser.getUsername())
                .claim("roles", authenticatedUser.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Obtiene del token JWT el id del usuario.
     * @param token El token jwt del usuario autenticado.
     * @return El id del usuario.
     */
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * Permite validar un token jwt.
     * @param authToken El token jwt a validar.
     * @return True si el token es válido, false en caso contrario.
     */
    public boolean validateToken(String authToken) {
        boolean valid = false;

        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey())
                    .build().parseClaimsJws(authToken);
            valid = true;
        } catch (MalformedJwtException ex) {
            logger.error("Este token JWT no es válido");
        }

        return valid;
    }
}
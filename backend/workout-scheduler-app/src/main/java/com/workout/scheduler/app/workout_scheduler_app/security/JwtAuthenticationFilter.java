package com.workout.scheduler.app.workout_scheduler_app.security;

import com.workout.scheduler.app.workout_scheduler_app.services.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Clase que actúa cómo filtro cada vez que se hace una petición.
 * Extrae el token JWT del header (Authorization), lo valida y
 * establece la autenticación en el contexto de seguridad de spring.
 * Este filtro se añade antes del filtro de autenticación por usuario / email
 * y contraseña de Spring.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Realiza el filtro de autenticación. Hace una búsqueda
     * del usuario por id y lo autentica, de contrario lanzará
     * un error.
     * @param request El objeto de la solicitud
     * @param response El objeto con la respuesta
     * @param filterChain El filtro de autenticación (lo que decide quien tiene acceso y quien no)
     * @throws ServletException En caso de que haya un error
     * @throws IOException En caso de que haya un error
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Ignorar Swagger y documentación pública
        String path = request.getRequestURI();

        if (path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromJWT(jwt);

                UserDetails userDetails = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("No se pudo autenticar al usuario en el contexto de seguridad", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Recupera el token JWT de la solicitud, de la cabecera http Authorization.
     * @param request El objeto que contiene la solicitud
     * @return El token JWT extraído de la cabecera
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            return bearerToken.substring(7);

        return null;
    }
}

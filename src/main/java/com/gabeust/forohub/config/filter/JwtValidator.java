package com.gabeust.forohub.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabeust.forohub.service.UserDetailsServiceImpl;
import com.gabeust.forohub.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Filtro personalizado para validar tokens JWT en cada petición HTTP entrante.
 *
 * Este filtro extrae el token JWT del encabezado Authorization, lo valida y,
 * si es correcto, establece la autenticación en el contexto de seguridad de Spring.
 */
public class JwtValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public JwtValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;

    }
    /**
     * Método principal del filtro que se ejecuta una vez por petición.
     *
     * @param request     Petición HTTP entrante.
     * @param response    Respuesta HTTP saliente.
     * @param filterChain Cadena de filtros a continuar si el token es válido.
     * @throws ServletException En caso de error de servlet.
     * @throws IOException      En caso de error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (jwtToken != null && jwtToken.toLowerCase().startsWith("bearer ")) {
            jwtToken = jwtToken.substring(7); // 7 porque "Bearer " tiene 7 caracteres

            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
                if (decodedJWT != null) {
                    // Extrae el email (subject) y los roles del token
                    String email = jwtUtils.extractUsername(decodedJWT);
                    String authoritiesClaim = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

                    // Convierte el string de roles separados por coma a una lista de authorities
                    Collection<? extends GrantedAuthority> authoritiesList =
                            authoritiesClaim != null ? AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim)
                                    : Collections.emptyList();

                    // Crea una autenticación basada en los datos extraídos del token
                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authoritiesList);

                    // Establece la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (Exception e) {
                // Bloquea la solicitud si el token es inválido
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }
        // Continúa con el resto de la cadena de filtros
        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/login/oauth2/code");
    }
}

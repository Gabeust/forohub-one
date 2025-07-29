package com.gabeust.forohub.config;

import com.gabeust.forohub.config.filter.JwtValidator;
import com.gabeust.forohub.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de seguridad principal de la aplicación.
 *
 * Define la política de seguridad general, incluyendo:
 * - Autenticación basada en JWT
 * - Inicio de sesión vía OAuth2 (Google, etc.)
 * - Desactivación de CSRF
 * - Filtros personalizados
 * - CORS y gestión de sesiones sin estado
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    @Lazy
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * - Desactiva CSRF
     * - Usa sesiones sin estado (JWT)
     * - Agrega filtro personalizado para validar JWT
     * - Habilita autenticación OAuth2 y manejo de errores
     *
     * @param httpSecurity el objeto HttpSecurity
     * @return la cadena de filtros configurada
     * @throws Exception en caso de error de configuración
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("http://localhost:5500/login?error=oauth2");
                        })
                )
                .addFilterBefore(new JwtValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }
    /**
     * Configura el CORS para permitir solicitudes desde el frontend.
     *
     * @return la configuración CORS
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5500", "http://127.0.0.1:5500"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    /**
     * Provee el AuthenticationManager del contexto.
     *
     * @param configuration configuración de autenticación
     * @return el AuthenticationManager configurado
     * @throws Exception si hay error al obtenerlo
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return  configuration.getAuthenticationManager();
    }
    /**
     * Codificador de contraseñas usando BCrypt.
     *
     * @return el PasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    /**
     * Proveedor de autenticación personalizado con servicio de usuario y codificador.
     *
     * @param userDetailsService servicio para cargar usuarios
     * @return el AuthenticationProvider configurado
     */
    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}

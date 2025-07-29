package com.gabeust.forohub.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.repository.IUserRepository;
import com.gabeust.forohub.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * Utilidad para la generación, validación e invalidación de tokens JWT.
 *
 * Define métodos para:
 * - Crear tokens JWT basados en el email del usuario o autenticación.
 * - Generar tokens temporales para recuperación de contraseña.
 * - Validar tokens, incluyendo verificación contra lista negra.
 * - Invalidar tokens manualmente.
 * - Extraer claims específicos o todos los claims de un token.
 */
@Component
public class JwtUtils {

    @Value("${spring.security.jwt.private.key}")
    private String privateKey;
    private final TokenBlacklistService tokenBlacklistService;
    private final IUserRepository userRepository;

    public JwtUtils(TokenBlacklistService tokenBlacklistService, IUserRepository userRepository) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.userRepository = userRepository;
    }

    /**
     * Crea un JWT a partir del email de un usuario registrado.
     *
     * @param email Email del usuario
     * @return JWT generado
     */
    public String createTokenFromEmail(String email) {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        String authorities = user.getRolesList().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(Collectors.joining(","));

        return JWT.create()
                .withSubject(email)
                .withClaim("userId", user.getId())
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 720000))// 2 horas
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }
    /**
     * Crea un token temporal para recuperación de contraseña (15 minutos).
     *
     * @param email Email del usuario
     * @return JWT de recuperación
     */
    public String createPasswordResetToken(String email) {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 900000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }
    /**
     * Valida un token JWT verificando firma, expiración y si está en lista negra.
     *
     * @param token Token JWT a validar
     * @return Token decodificado si es válido
     * @throws JWTVerificationException si es inválido o está expirado
     */
    public DecodedJWT validateToken(String token) {
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            throw new JWTVerificationException("Invalid or expired token");
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // Verificar manualmente si el token ha expirado
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new JWTVerificationException("expired token");
            }

            return decodedJWT;
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(" Invalid token, Unauthorized");
        }

    }
    /**
     * Invalida un token añadiéndolo a la lista negra hasta su expiración.
     *
     * @param token Token JWT a invalidar
     */
    public void invalidateToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        long expiresIn = decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis();
        tokenBlacklistService.blacklistToken(token, expiresIn);
    }
    /**
     * Extrae el nombre de usuario (email) del token decodificado.
     *
     * @param decodedJWT Token decodificado
     * @return Email del usuario
     */
    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }
    /**
     * Obtiene un claim específico del token.
     *
     * @param decodedJWT Token decodificado
     * @param claimName  Nombre del claim
     * @return Claim correspondiente
     */
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }
    /**
     * Retorna todos los claims contenidos en el token decodificado.
     *
     * @param decodedJWT Token decodificado
     * @return Mapa de claims
     */
    public Map<String, Claim> returnAllClaim(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
    /**
     * Crea un token a partir del objeto Authentication proporcionado por Spring Security.
     *
     * @param authentication Objeto de autenticación
     * @return JWT generado
     */
    public String createToken(Authentication authentication) {
        return createTokenFromEmail(authentication.getName());
    }
}

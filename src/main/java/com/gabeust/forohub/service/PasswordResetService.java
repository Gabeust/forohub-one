package com.gabeust.forohub.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.repository.IUserRepository;
import com.gabeust.forohub.util.JwtUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Servicio para manejar el flujo de restablecimiento de contraseña.
 *
 * Incluye creación y validación de tokens JWT específicos para reset de contraseña,
 * así como la actualización segura de la contraseña del usuario.
 */
@Service
public class PasswordResetService {
    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    public PasswordResetService(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;

    }
    /**
     * Genera un token JWT para restablecer la contraseña de un usuario dado su email.
     *
     * @param email correo del usuario
     * @return token JWT para reset password
     * @throws UsernameNotFoundException si no existe un usuario con ese email
     */
    public String createResetToken(String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with that email");
        }
        return jwtUtils.createPasswordResetToken(email);
    }
    /**
     * Valida si un token de restablecimiento es válido y no está expirado.
     *
     * @param token token JWT para reset password
     * @return true si es válido y no expirado, false en caso contrario
     */
    public Boolean IsValidToken(String token){
        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(token);
            return decodedJWT.getExpiresAt().after(new Date());
        }catch (JWTVerificationException e){
            return false; // Token no valido o expirado.
        }
    }
    /**
     * Cambia la contraseña de un usuario si el token es válido.
     * Reactiva la cuenta y reinicia el contador de intentos fallidos.
     *
     * @param token token JWT para reset password
     * @param newPassword nueva contraseña a establecer
     * @throws IllegalArgumentException si el token es inválido o expirado
     * @throws UsernameNotFoundException si no se encuentra el usuario
     */
    public void resetPassword(String token, String newPassword) {
        if (!IsValidToken(token)) {
            throw new IllegalArgumentException("Token not valid or expired");
        }
        //Extrae el mail del token
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String email = decodedJWT.getSubject();
        //busca al usuario por email
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with that email");
        }
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);

        // Reactiva la cuenta y credenciales
        user.setAccountNotLocked(true); // Desbloquear la cuenta
        user.setCredentialNotExpired(true); // Credenciales activas
        user.setFailedAttempts(0); // Reiniciar intentos fallidos

        userRepository.save(user);
    }
}

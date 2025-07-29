package com.gabeust.forohub.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gabeust.forohub.dto.*;
import com.gabeust.forohub.entity.Role;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.service.*;
import com.gabeust.forohub.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Controlador REST para autenticación y operaciones relacionadas con la seguridad.
 *
 * Provee endpoints para login, logout, cambio y restablecimiento de contraseña.
 */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordResetService passwordResetService;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImpl roleService;

    public AuthController(UserDetailsServiceImpl userDetailsService, PasswordResetService passwordResetService, JwtUtils jwtUtils, EmailService emailService, UserServiceImpl userService, PasswordEncoder passwordEncoder, RoleServiceImpl roleService) {
        this.userDetailsService = userDetailsService;
        this.passwordResetService = passwordResetService;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }
    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Asigna por defecto el rol USER. Valida que el email no esté registrado,
     * la contraseña no esté vacía y encripta la contraseña antes de guardar.
     *
     * @param registerRequest datos del nuevo usuario
     * @return datos del usuario creado o error
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        // Validar email
        if (userService.existsByEmail(registerRequest.email())) {
            return ResponseEntity.badRequest().body("Email is already registered.");
        }

        // Validar contraseña
        if (registerRequest.password() == null || registerRequest.password().isBlank()) {
            return ResponseEntity.badRequest().body("Password cannot be empty.");
        }

        // Buscar rol USER
        Optional<Role> userRoleOpt = roleService.findByName("USER");
        if (userRoleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Default role USER not found in the system.");
        }

        // Crear usuario
        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(userService.encriptPassword(registerRequest.password()));
        user.setRolesList(Set.of(userRoleOpt.get()));

        // Guardar usuario
        User newUser = userService.save(user);

        // Retornar respuesta sin contraseña
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", newUser.getId(),
                "email", newUser.getEmail(),
                "roles", newUser.getRolesList().stream().map(Role::getName).toList(),
                "message", "User registered successfully."
        ));
    }

    /**
     * Auténtica al usuario y genera un token JWT si las credenciales son válidas.
     *
     * @param loginRequestDTO credenciales del usuario
     * @return token y datos del usuario autenticado
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDTO loginRequestDTO) {
        try {
            AuthResponseDTO response = this.userDetailsService.loginUser(loginRequestDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            // Esto lo podés adaptar según el tipo de excepción real que lances desde loginUser()
            String errorMessage = ex.getMessage();

            if (errorMessage.toLowerCase().contains("bloqueado")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Account locked due to multiple failed login attempts."));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", errorMessage != null ? errorMessage :  "Invalid credentials."));
        }
    }

    /**
     * Invalida el token JWT del usuario (logout).
     *
     * @param token el token JWT del encabezado Authorization
     * @return Mensaje de éxito
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        jwtUtils.invalidateToken(jwt);
        return ResponseEntity.ok("Logged out successfully.!");
    }
    /**
     * Solicita el restablecimiento de contraseña para el email dado.
     * Envia un enlace por correo con un token válido por 15 minutos.
     *
     * @param emailRequestDTO contiene el email del usuario
     * @return Mensaje indicando si se envió el enlace
     */
    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody EmailRequestDTO emailRequestDTO) {
        String email = emailRequestDTO.email();
        try {
            String token = passwordResetService.createResetToken(email);
            String resetLink = "http://localhost:5500/index.html?token=" + token;;
            // Enviar correo con el enlace
            emailService.sendEmail(email, "Reset Password",  "Use the following link to reset your password: " + resetLink);
            return ResponseEntity.ok("A password reset link has been sent to your email.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body("User with that email was not found.");
        }
    }
    /**
     * Restablece la contraseña del usuario usando un token de recuperación.
     *
     * @param token token JWT válido
     * @param passwordDTO nueva contraseña
     * @return mensaje de éxito o error
     */
    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody PasswordDTO passwordDTO) {
        try {
            passwordResetService.resetPassword(token, passwordDTO.newPassword());
            return ResponseEntity.ok("Password reset successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid or expired token.");
        }
    }
    /**
     * Permite a un usuario autenticado cambiar su contraseña actual.
     *
     * @param request objeto con contraseña actual y nueva
     * @param authentication contexto de autenticación (obtenido automáticamente)
     * @return Mensaje indicando el resultado del cambio
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDTO request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authenticated.");
        }
        // Obtiene el email del usuario autenticado

        String email = authentication.getName();

        // Busca usuario en la base de datos
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        // Verifica la contraseña actual
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect current password.");
        }
        // Actualiza la contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userService.save(user);

        return ResponseEntity.ok("Password changed successfully.");
    }
    /**
     * Válida un token JWT (por ejemplo, para confirmar si aún es válido).
     *
     * @param token token JWT a validar
     * @return 200 OK si es válido, 401 Unauthorized si es inválido o expirado
     */
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestParam("token") String token) {
        try {
            jwtUtils.validateToken(token);
            return ResponseEntity.ok().build(); // Token válido
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Token inválido
        }
    }

}

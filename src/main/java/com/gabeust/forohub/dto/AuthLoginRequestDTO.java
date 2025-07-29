package com.gabeust.forohub.dto;

import jakarta.validation.constraints.NotBlank;
/**
 * DTO utilizado para solicitar autenticación (login) de un usuario.
 *
 * Contiene el email y la contraseña necesarios para el inicio de sesión.
 *
 * Ambos campos deben estar presentes y no vacíos.
 *
 * @param email    el correo electrónico del usuario
 * @param password la contraseña del usuario
 */
public record AuthLoginRequestDTO(@NotBlank String email, @NotBlank String password) {
}


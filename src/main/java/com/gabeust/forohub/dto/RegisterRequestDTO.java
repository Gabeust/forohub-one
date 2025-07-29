package com.gabeust.forohub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de registro de nuevos usuarios.
 *
 * Contiene el email y la contraseña que el usuario desea usar para crear la cuenta.
 * Ambos campos son obligatorios y no pueden estar vacíos.
 *
 * @param email    correo electrónico para el nuevo usuario
 * @param password contraseña para el nuevo usuario
 */
public record RegisterRequestDTO(@NotBlank String email,
                                 @NotBlank String password) {
}

package com.gabeust.forohub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de cambio de contraseña.
 *
 * Contiene la contraseña actual y la nueva contraseña que el usuario desea establecer.
 *
 * @param currentPassword la contraseña actual del usuario
 * @param newPassword     la nueva contraseña que se desea establecer
 */
public record ChangePasswordRequestDTO(@NotBlank String currentPassword,@NotBlank String newPassword) {
}

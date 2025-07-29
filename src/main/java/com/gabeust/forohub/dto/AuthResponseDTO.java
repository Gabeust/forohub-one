package com.gabeust.forohub.dto;

/**
 * DTO que representa la respuesta luego de un intento de autenticación.
 *
 * Contiene el email del usuario, un mensaje informativo, el token JWT generado,
 * y un estado booleano que indica si la autenticación fue exitosa o no.
 *
 * @param email   correo electrónico del usuario autenticado
 * @param message mensaje descriptivo del resultado de la autenticación
 * @param jwt     token JWT generado para el usuario
 * @param status  indica si la autenticación fue exitosa (true) o fallida (false)
 */
public record AuthResponseDTO (String email, String message, String jwt, boolean status){
}

package com.gabeust.forohub.controller;

import com.gabeust.forohub.dto.ProfileDTO;
import com.gabeust.forohub.service.ProfileServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
/**
 * Controlador REST para gestionar los perfiles de usuario.
 *
 * Permite obtener, crear, actualizar y eliminar perfiles asociados a un usuario.
 */
@RestController
@RequestMapping("api/v1/profiles")
public class ProfileController {

    private final ProfileServiceImpl profileService;

    public ProfileController(ProfileServiceImpl profileService) {
        this.profileService = profileService;
    }
    /**
     * Obtiene el perfil asociado a un usuario dado su ID.
     *
     * @param userId ID del usuario
     * @return el perfil del usuario si existe, o 404 Not Found si no se encuentra
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getByUserId(@PathVariable Long userId) {
        return profileService.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crea un nuevo perfil para un usuario.
     *
     * @param profileDTO datos del perfil a crear
     * @param uriBuilder para construir la URI del recurso creado
     * @return 201 Created con el perfil creado
     */
    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile(@Valid @RequestBody ProfileDTO profileDTO, UriComponentsBuilder uriBuilder) {
        ProfileDTO savedProfile = profileService.save(profileDTO);

        URI location = uriBuilder
                .path("/api/v1/profiles/{id}")
                .buildAndExpand(savedProfile.userId())
                .toUri();

        return ResponseEntity.created(location).body(savedProfile);
    }
    /**
     * Actualiza el perfil de un usuario.
     *
     * @param dto perfil actualizado
     * @return 200 OK con el perfil actualizado
     */
    @PutMapping("/{userid}")
    public ResponseEntity<ProfileDTO> updateProfile(@RequestBody ProfileDTO dto) {
        return ResponseEntity.ok(profileService.save(dto));
    }

    /**
     * Elimina el perfil asociado a un usuario.
     *
     * @param userId ID del usuario
     * @return 204 No Content si se eliminó, o 404 Not Found si no existía
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        if (profileService.findByUserId(userId).isPresent()) {
            profileService.deleteByUserId(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

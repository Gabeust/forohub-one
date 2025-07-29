package com.gabeust.forohub.controller;

import com.gabeust.forohub.dto.ReactionDTO;
import com.gabeust.forohub.service.ReactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
/**
 * Controlador REST para gestionar las reacciones de los usuarios (like, dislike, etc.)
 *
 */
@RestController
@RequestMapping("api/v1/reactions")
public class ReactionController {

    private final ReactionServiceImpl reactionService;

    public ReactionController(ReactionServiceImpl reactionService) {
        this.reactionService = reactionService;
    }

    /**
     * Obtiene todas las reacciones existentes.
     *
     * @return lista de reacciones en formato DTO
     */
    @GetMapping
    public ResponseEntity<List<ReactionDTO>> getAllReactions() {
        return ResponseEntity.ok(reactionService.findAll());
    }
    /**
     * Obtiene una reacción por su ID.
     *
     * @param id ID de la reacción
     * @return la reacción encontrada, o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReactionDTO> getReactionById(@PathVariable Long id) {
        return reactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crea una nueva reacción o elimina la existente si el usuario reacciona dos veces.
     *
     * @param reactionDTO datos de la reacción
     * @param uriBuilder para construir la URI del nuevo recurso
     * @return 201 Created si se crea, 204 No Content si se elimina
     */
    @PostMapping
    public ResponseEntity<ReactionDTO> createReaction(@Valid @RequestBody ReactionDTO reactionDTO,
                                                      UriComponentsBuilder uriBuilder) {
        ReactionDTO savedReaction = reactionService.save(reactionDTO);

        if (savedReaction == null) {
            return ResponseEntity.noContent().build(); // 💡 reacción eliminada
        }

        URI location = uriBuilder
                .path("/api/v1/reactions/{id}")
                .buildAndExpand(savedReaction.id())
                .toUri();

        return ResponseEntity.created(location).body(savedReaction);
    }
    /**
     * Elimina una reacción por su ID.
     *
     * @param id ID de la reacción
     * @return 204 No Content si se elimina, 404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Long id) {
        if (reactionService.findById(id).isPresent()) {
            reactionService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

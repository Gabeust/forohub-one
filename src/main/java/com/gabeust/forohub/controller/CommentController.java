package com.gabeust.forohub.controller;

import com.gabeust.forohub.dto.CommentDTO;
import com.gabeust.forohub.service.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
/**
 * Controlador REST para la gestión de comentarios.
 *
 * Provee endpoints para crear, listar, obtener por ID, listar comentarios por post y eliminar comentarios.
 */
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    /**
     * Obtiene la lista de todos los comentarios.
     *
     * @return lista de comentarios
     */
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(){
        return ResponseEntity.ok(commentService.findAll());
    }
    /**
     * Obtiene un comentario por su ID.
     *
     * @param id ID del comentario
     * @return comentario si existe, 404 si no
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Obtiene todos los comentarios asociados a un post específico.
     *
     * @param postId ID del post
     * @return lista de comentarios del post
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findByPostId(postId));
    }
    /**
     * Crea un nuevo comentario.
     *
     * @param commentDTO datos del comentario a crear
     * @param uriBuilder constructor para crear la URI del recurso creado
     * @return comentario creado con ubicación en header Location
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentDTO commentDTO, UriComponentsBuilder uriBuilder) {
        CommentDTO savedComment = commentService.save(commentDTO);

        URI location = uriBuilder
                .path("/api/v1/comments/{id}")
                .buildAndExpand(savedComment.id())
                .toUri();

        return ResponseEntity.created(location).body(savedComment);
    }
    /**
     * Elimina un comentario por su ID.
     *
     * @param id ID del comentario a eliminar
     * @return 204 No Content si se eliminó, 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        if (commentService.findById(id).isPresent()) {
            commentService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

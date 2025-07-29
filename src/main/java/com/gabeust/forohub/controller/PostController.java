package com.gabeust.forohub.controller;

import com.gabeust.forohub.dto.PageDTO;
import com.gabeust.forohub.dto.PostDTO;
import com.gabeust.forohub.dto.UserStatsDTO;
import com.gabeust.forohub.service.PostServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para la gestión de posts.
 *
 * Provee endpoints para crear, listar, obtener, actualizar y eliminar posts,
 * así como para listar posts por categoría, autor y obtener estadísticas de usuario.
 */
@RestController
@RequestMapping("api/v1/posts")
public class PostController {

    private final PostServiceImpl postService;

    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }
    /**
     * Obtiene una página de posts paginados y ordenados según los parámetros enviados.
     *
     * @param page      Número de página a obtener (0-indexado), por defecto 0.
     * @param size      Cantidad de posts por página, por defecto 10.
     * @param sortBy    Campo por el cual ordenar, por defecto "createdAt".
     * @param direction Dirección del ordenamiento ("asc" o "desc"), por defecto "desc".
     * @return Un PageDTO con la lista de posts y la información de paginación.
     */
    @GetMapping
    public PageDTO<PostDTO> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.findAllPaged(page, size, sortBy, direction);

    }
    /**
     * Obtiene un post por su ID.
     *
     * @param id ID del post
     * @return post si existe, 404 si no
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return postService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Obtiene una página de publicaciones filtradas por categoría.
     *
     * @param categoryId ID de la categoría
     * @param page número de página (por defecto 0)
     * @param size tamaño de página (por defecto 10)
     * @param sortBy campo por el cual ordenar (por defecto "createdAt")
     * @param direction dirección de orden ("asc" o "desc", por defecto "desc")
     * @return página de publicaciones como PageDTO<PostDTO>
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageDTO<PostDTO>> getPostsByCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageDTO<PostDTO> result = postService.findByCategoryIdPaged(categoryId, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene los posts de un autor específico.
     *
     * @param authorId ID del autor
     * @return lista de posts o 204 No Content si no hay posts
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostDTO>> getPostsByAuthor(@PathVariable Long authorId) {
        List<PostDTO> posts = postService.findByAuthorId(authorId);
        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(posts);
    }
    /**
     * Obtiene estadísticas del usuario, como cantidad de posts, reacciones, etc.
     *
     * @param id ID del usuario
     * @return estadísticas del usuario
     */
    @GetMapping("/user/{id}/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getStatsByUserId(id));
    }
    /**
     * Crea un nuevo post.
     *
     * @param postDTO datos del post a crear
     * @param uriBuilder constructor para crear la URI del recurso creado
     * @return post creado con ubicación en header Location
     */
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO postDTO, UriComponentsBuilder uriBuilder) {
        PostDTO savedPost = postService.save(postDTO);

        URI location = uriBuilder
                .path("/api/v1/posts/{id}")
                .buildAndExpand(savedPost.id())
                .toUri();

        return ResponseEntity.created(location).body(savedPost);
    }

    /**
     * Actualiza un post existente por ID.
     *
     * @param id ID del post a actualizar
     * @param postDTO datos actualizados del post
     * @return post actualizado o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        PostDTO updatedPost = postService.update(id, postDTO);
        if (updatedPost == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPost);
    }
    /**
     * Elimina un post por su ID.
     *
     * @param id ID del post a eliminar
     * @return 204 No Content si se eliminó, 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (postService.findById(id).isPresent()) {
            postService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

package com.gabeust.forohub.controller;

import com.gabeust.forohub.dto.CategoryDTO;
import com.gabeust.forohub.service.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para la gestión de categorías.
 *
 * Provee endpoints para crear, listar, obtener por ID y eliminar categorías.
 */
@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }
    /**
     * Obtiene la lista de todas las categorías.
     *
     * @return ResponseEntity con la lista de CategoryDTO y status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(){
        return ResponseEntity.ok(categoryService.findAll());
    }
    /**
     * Obtiene una categoría por su ID.
     *
     * @param id ID de la categoría a buscar.
     * @return ResponseEntity con el CategoryDTO y status 200 OK si se encuentra,
     *         o status 404 Not Found si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crea una nueva categoría.
     *
     * @param categoryDTO Datos de la categoría a crear.
     * @param uriBuilder Utilizado para construir la URI del recurso creado.
     * @return ResponseEntity con el CategoryDTO creado, status 201 Created,
     *         y header Location apuntando al nuevo recurso.
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, UriComponentsBuilder uriBuilder) {
        CategoryDTO savedCategory = categoryService.save(categoryDTO);

        URI location = uriBuilder
                .path("api/v1/categories/{id}")
                .buildAndExpand(savedCategory.id())
                .toUri();

        return ResponseEntity.created(location).body(savedCategory);
    }
    /**
     * Elimina una categoría por su ID.
     *
     * @param id ID de la categoría a eliminar.
     * @return ResponseEntity con status 204 No Content si se elimina correctamente,
     *         o status 404 Not Found si no existe la categoría.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (categoryService.findById(id).isPresent()) {
            categoryService.deletebyId(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

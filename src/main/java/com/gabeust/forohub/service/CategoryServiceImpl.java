package com.gabeust.forohub.service;

import com.gabeust.forohub.dto.CategoryDTO;
import com.gabeust.forohub.entity.Category;
import com.gabeust.forohub.mapper.CategoryMapper;
import com.gabeust.forohub.repository.ICategoryRepository;
import com.gabeust.forohub.service.interf.ICategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio para gestionar las categorías del foro.
 *
 * Proporciona métodos para buscar, guardar y eliminar categorías mediante el repositorio.
 */

@Service
public class CategoryServiceImpl implements ICategoriaService {

    private final ICategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(ICategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }


    /**
     * Obtiene una lista de todas las categorías existentes.
     *
     * @return lista de categorías como DTOs
     */
    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO).toList();
    }
    /**
     * Busca una categoría por su ID.
     *
     * @param id ID de la categoría
     * @return un Optional con el DTO si existe, o vacío si no se encuentra
     */
    @Override
    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO);
    }
    /**
     * Busca una categoría por su nombre.
     *
     * @param name nombre de la categoría
     * @return un Optional con el DTO si existe, o vacío si no se encuentra
     */
    @Override
    public Optional<CategoryDTO> findByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toDTO);
    }
    /**
     * Guarda una nueva categoría o actualiza una existente.
     *
     * @param categoryDTO DTO con los datos de la categoría a guardar
     * @return DTO de la categoría guardada
     */
    @Transactional
    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category saved = categoryRepository.save(category);

        return categoryMapper.toDTO(saved);
    }
    /**
     * Elimina una categoría por su ID.
     *
     * @param id ID de la categoría a eliminar
     */
    @Override
    public void deletebyId(Long id) {
        categoryRepository.deleteById(id);
    }
}

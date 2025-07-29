package com.gabeust.forohub.service.interf;

import com.gabeust.forohub.dto.CategoryDTO;

import java.util.List;
import java.util.Optional;

public interface ICategoriaService {

    List<CategoryDTO> findAll();
    Optional<CategoryDTO> findById(Long id);
    Optional<CategoryDTO> findByName(String name);
    CategoryDTO save(CategoryDTO categoryDTO);
    void deletebyId(Long id);

}

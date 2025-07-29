package com.gabeust.forohub.mapper;


import com.gabeust.forohub.dto.CategoryDTO;
import com.gabeust.forohub.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);
    Category toEntity(CategoryDTO categoryDTO);
}

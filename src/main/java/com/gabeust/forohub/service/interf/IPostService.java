package com.gabeust.forohub.service.interf;

import com.gabeust.forohub.dto.PageDTO;
import com.gabeust.forohub.dto.PostDTO;

import java.util.List;
import java.util.Optional;

public interface IPostService {
    PageDTO<PostDTO> findAllPaged(int page, int size, String sortBy, String direction);
    Optional<PostDTO> findById(Long id);
    PostDTO save(PostDTO postDTO);
    PostDTO update(Long id, PostDTO postDTO);
    List<PostDTO> findByCategoryId(Long categoryId);
    List<PostDTO> findByAuthorId(Long authorId);
    void deleteById(Long id);
}

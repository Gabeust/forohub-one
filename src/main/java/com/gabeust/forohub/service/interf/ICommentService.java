package com.gabeust.forohub.service.interf;

import com.gabeust.forohub.dto.CommentDTO;

import java.util.List;
import java.util.Optional;

public interface ICommentService {
    List<CommentDTO> findAll();
    Optional<CommentDTO> findById(Long id);
    CommentDTO save(CommentDTO commentDTO);
    void deleteById(Long id);

}

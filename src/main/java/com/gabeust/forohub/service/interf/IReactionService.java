package com.gabeust.forohub.service.interf;

import com.gabeust.forohub.dto.ReactionDTO;

import java.util.List;
import java.util.Optional;

public interface IReactionService {
    List<ReactionDTO> findAll();
    Optional<ReactionDTO> findById(Long id);
    ReactionDTO save(ReactionDTO reactionDTO);
    void deleteById(Long id);
}

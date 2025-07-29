package com.gabeust.forohub.repository;

import com.gabeust.forohub.entity.Reaction;
import com.gabeust.forohub.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReactionRepository extends JpaRepository<Reaction, Long> {
    long countByPostIdAndReactionType(Long postId, ReactionType reactionType);
    boolean existsByUserIdAndPostIdAndReactionType(Long userId, Long postId, ReactionType reactionType);
    Optional<Reaction> findByUserIdAndPostId(Long userId, Long postId);
    int countByUserId(Long userId);

}

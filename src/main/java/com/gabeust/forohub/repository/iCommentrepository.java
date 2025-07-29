package com.gabeust.forohub.repository;

import com.gabeust.forohub.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface iCommentrepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_IdOrderByCreatedAtDesc(Long postId);
    int countByAuthorId(Long userId);

}

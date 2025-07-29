package com.gabeust.forohub.repository;

import com.gabeust.forohub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategoryId(Long categoryId);
    int countByAuthorId(Long userId);
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);
    List<Post> findByAuthorId(Long authorId);
}

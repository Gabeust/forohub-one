package com.gabeust.forohub.dto;

import com.gabeust.forohub.enums.ReactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

public record PostDTO(Long id,
                      @NotBlank String title,
                      @NotBlank String content,
                      @NotBlank String authorNick,
                      @NotNull Long authorId,
                      @NotBlank String authorImage,
                      @NotBlank String categoryName,
                      LocalDateTime createdAt,
                      Map<ReactionType, Long> reactions) {}

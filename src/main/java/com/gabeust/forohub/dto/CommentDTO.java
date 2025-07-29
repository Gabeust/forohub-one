package com.gabeust.forohub.dto;


import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CommentDTO(           Long id,
                          @NotBlank String content,
                                    LocalDateTime createdAt,
                          @NotBlank String authorNick,
                          @NotBlank Long postId) {
}

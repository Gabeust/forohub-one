package com.gabeust.forohub.dto;

import com.gabeust.forohub.enums.ReactionType;

public record ReactionDTO(Long id,
                          ReactionType reactionType,
                          String nick,
                          Long postId) {
}

package com.gabeust.forohub.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ProfileDTO(
                         @NotBlank String nick,
                         String image,
                         @NotNull
                         @Size(max = 1500)
                         String bio,
                         LocalDateTime createdAt,
                         Long userId) {
}

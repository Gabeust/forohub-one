package com.gabeust.forohub.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO (Long id, @NotBlank String name){
}

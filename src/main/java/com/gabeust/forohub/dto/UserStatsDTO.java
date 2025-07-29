package com.gabeust.forohub.dto;

public record UserStatsDTO(
                            int totalPosts,
                            int totalReactions,
                            int totalComments
) {}
package com.gabeust.forohub.dto;

public record NotificationEvent(
        Long recipientUserId,
        String type,
        String message,
        Long postId,
        Long commentId,
        Long reactionId
) {}


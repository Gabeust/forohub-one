package com.gabeust.forohub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientUserId;
    private String type; // COMMENT, REACTION
    private String message;

    private Long postId;
    private Long commentId;
    private Long reactionId;
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();

}

package kr.co.promptech.springboottutorial.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostRejection {
    private Long id;
    private Long postId;
    private String reason;
    private LocalDateTime createdAt;
}
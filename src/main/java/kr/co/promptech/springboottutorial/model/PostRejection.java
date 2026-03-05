package kr.co.promptech.springboottutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostRejection {
    private Long id;
    private Long postId;
    private String reason;
    private Long rejectedBy;
    private LocalDateTime createdAt;
}
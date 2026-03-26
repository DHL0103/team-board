package kr.co.promptech.springboottutorial.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostRejectionDto {
    private Long id;
    private Long postId;
    private String reason;
    private Long rejectedBy;
    private String rejectorName;
    private LocalDateTime createdAt;
}

package kr.co.promptech.springboottutorial.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostRejection {
    private Long id;
    private Long postId;             // 반려된 업무 ID (FK → posts.id)
    private String reason;           // 반려 사유
    private Long rejectedBy;         // 반려한 관리자 ID (FK → members.id)
    private LocalDateTime createdAt; // 반려 일시
}

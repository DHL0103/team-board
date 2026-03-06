package kr.co.promptech.springboottutorial.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long postId;             // 소속 업무 ID (FK → posts.id)
    private Long memberId;           // 작성자 ID (FK → members.id)
    private String content;          // 댓글 내용
    private LocalDateTime createdAt; // 작성일시
    private Long parentId;           // 부모 댓글 ID (null이면 최상위)
    private Long groupId;            // 최상위 부모 ID (정렬용)
    private int depth;               // 계층 깊이 (0: 최상위)
    private boolean isDeleted;       // 논리적 삭제 여부
}

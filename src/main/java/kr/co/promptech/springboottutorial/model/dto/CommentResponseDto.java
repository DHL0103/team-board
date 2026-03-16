package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class    CommentResponseDto {
    private Long id;
    private Long postId;
    private Long memberId;
    private String authorUsername;
    private String parentAuthorUsername; // depth 1일 때 @표시용
    private String content;
    private LocalDateTime createdAt;
    private Long parentId;
    private Long groupId;
    private int depth;
    private boolean isDeleted;
}
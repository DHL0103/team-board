package kr.co.promptech.springboottutorial.model.dto;

import kr.co.promptech.springboottutorial.model.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private final Long id;
    private final Long boardId;
    private final Long memberId;
    private final String title;
    private final String content;
    private final String status;
    private final LocalDateTime dueDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.boardId = post.getBoardId();
        this.memberId = post.getMemberId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.status = post.getStatus();
        this.dueDate = post.getDueDate();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}

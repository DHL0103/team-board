package kr.co.promptech.springboottutorial.post;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private Long boardId;       // 어느 팀 게시판인지
    private Long memberId;        // 작성자
    private String title;
    private String content;
    private String status;      // PROGRESS, REQUESTED, COMPLETED
    private LocalDateTime dueDate;   // 마감 기한
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

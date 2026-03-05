package kr.co.promptech.springboottutorial.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

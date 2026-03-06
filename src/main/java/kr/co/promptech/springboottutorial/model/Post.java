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
    private Long boardId;            // 소속 게시판 ID (FK → boards.id)
    private Long memberId;           // 작성자 ID (FK → members.id)
    private String title;            // 업무 제목
    private String content;          // 업무 내용
    private String status;           // 업무 상태 (PROGRESS / REQUESTED / COMPLETED / REJECTED)
    private LocalDateTime dueDate;   // 마감 기한
    private LocalDateTime createdAt; // 작성일시
    private LocalDateTime updatedAt; // 수정일시
}

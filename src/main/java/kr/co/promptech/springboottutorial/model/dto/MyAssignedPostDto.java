package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyAssignedPostDto {
    private Long id;
    private Long boardId;
    private String boardName;
    private String boardColor;
    private String title;
    private String status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
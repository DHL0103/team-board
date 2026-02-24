package kr.co.promptech.springboottutorial.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostCreateDto {
    Long boardId;
    String title;
    String content;
    LocalDateTime dueDate;
}

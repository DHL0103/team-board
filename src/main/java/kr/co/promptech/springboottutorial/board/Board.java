package kr.co.promptech.springboottutorial.board;


import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
}

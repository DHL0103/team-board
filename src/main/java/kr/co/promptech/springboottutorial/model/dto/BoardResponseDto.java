package kr.co.promptech.springboottutorial.model.dto;

import kr.co.promptech.springboottutorial.model.Board;
import lombok.Getter;

@Getter
public class BoardResponseDto {
    private Long id;
    private String name;
    private String description;
    private String color;
    private String status;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.description = board.getDescription();
        this.color = board.getColor();
        this.status = board.getStatus();
    }
}

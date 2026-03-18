package kr.co.promptech.springboottutorial.model.dto;

import kr.co.promptech.springboottutorial.model.Board;
import lombok.Getter;

@Getter
public class BoardResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final String color;
    private final String status;
    private final long memberCount;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.description = board.getDescription();
        this.color = board.getColor();
        this.status = board.getStatus() != null ? board.getStatus().name() : null;
        this.memberCount = 0;
    }

    public BoardResponseDto(Long id, String name, String description, String color, String status, long memberCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.status = status;
        this.memberCount = memberCount;
    }
}

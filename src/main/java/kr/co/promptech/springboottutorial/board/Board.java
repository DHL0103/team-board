package kr.co.promptech.springboottutorial.board;

import lombok.Data;

@Data
public class Board {
    private Long id;
    private String name;        // 팀명
    private String slug;        // URL용 이름
    private String description; // 팀 설명
}

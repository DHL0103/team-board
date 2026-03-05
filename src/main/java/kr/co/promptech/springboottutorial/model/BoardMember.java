package kr.co.promptech.springboottutorial.model;

import lombok.Data;

@Data
public class BoardMember {
    private Long id;
    private Long boardId;
    private Long memberId;
    private String boardRole; // REQUESTED / USER / MANAGER
}

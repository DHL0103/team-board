package kr.co.promptech.springboottutorial.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardMember {
    private Long id;
    private Long boardId;       // 게시판 ID (FK → boards.id)
    private Long memberId;      // 회원 ID (FK → members.id)
    private String boardRole;   // 게시판 내 역할 (REQUESTED / USER / MANAGER)
}

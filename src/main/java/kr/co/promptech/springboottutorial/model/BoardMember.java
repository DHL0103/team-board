package kr.co.promptech.springboottutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardMember {
    private Long id;
    private Long boardId;
    private Long memberId;
    private String boardRole; // REQUESTED / USER / MANAGER
}

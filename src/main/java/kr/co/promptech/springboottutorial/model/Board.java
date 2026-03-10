package kr.co.promptech.springboottutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private Long id;
    private String name;        // 게시판 이름 (예: Backend팀)
    private String color;       // 테마 색상 (p1~p6)
    private String description; // 게시판 설명
    private String status;      // 게시판 상태 (ACTIVE / INACTIVE)
}

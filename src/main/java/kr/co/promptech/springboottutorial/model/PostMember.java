package kr.co.promptech.springboottutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostMember {
    private Long id;
    private Long postId;   // 담당 업무 ID (FK → posts.id)
    private Long memberId; // 담당자 ID (FK → members.id)
}

package kr.co.promptech.springboottutorial.model;

import lombok.Data;

@Data
public class PostMember {
    private Long id;
    private Long postId;
    private Long memberId;
}

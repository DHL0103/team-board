package kr.co.promptech.springboottutorial.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId;      // 어떤 업무에 달린 댓글인지
    private Long memberId;      // 누가 썼는지 (사장님 혹은 팀원)
    private String content;   // 피드백 내용
    private LocalDateTime createdAt;
}
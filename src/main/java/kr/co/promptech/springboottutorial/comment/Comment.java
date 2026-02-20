package kr.co.promptech.springboottutorial.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId;      // 어떤 업무에 달린 댓글인지
    private Long memberId;      // 누가 썼는지 (사장님 혹은 팀원)
    private String content;   // 피드백 내용
    private LocalDateTime createdAt;

    // (선택사항) 화면에 작성자 이름을 바로 보여주고 싶다면 추가
    private String writerName;
}
package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCreateDto {
    private Long postId;
    private String content;
    private Long parentId;
    private Long groupId;
    private int depth;
}
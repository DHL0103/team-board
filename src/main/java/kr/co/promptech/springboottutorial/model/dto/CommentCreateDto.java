package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    private Long postId;
    private String content;
    private Long parentId;
    private Long groupId;
    private int depth;
}
package kr.co.promptech.springboottutorial.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long postId;
    @NotBlank
    @Size(max = 256)
    private String content;
    private Long parentId;
    private Long groupId;
    private int depth;
}
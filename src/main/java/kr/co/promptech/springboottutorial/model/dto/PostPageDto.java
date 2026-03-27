package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageDto {
    private List<PostResponseDto> posts;
    private boolean hasMore;
    private long totalCount;
}

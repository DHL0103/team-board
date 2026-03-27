package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MemberPageDto {
    private List<MemberResponseDto> members;
    private boolean hasMore;
    private long totalCount;
}

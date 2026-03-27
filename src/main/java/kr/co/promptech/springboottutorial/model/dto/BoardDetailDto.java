package kr.co.promptech.springboottutorial.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardDetailDto {
    private Long id;
    private String name;
    private String description;
    private String color;
    private String status;
    private long memberCount;
    private boolean isManager;
    private List<BoardMemberResponseDto> boardUserList;
}
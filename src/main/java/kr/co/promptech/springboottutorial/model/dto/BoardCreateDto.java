package kr.co.promptech.springboottutorial.model.dto;

import lombok.Data;

@Data
public class BoardCreateDto {
    private String name;        // 팀명
    private String slug;        // URL용 이름
    private String description; // 팀 설명
}

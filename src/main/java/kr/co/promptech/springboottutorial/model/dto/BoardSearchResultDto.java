package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchResultDto {
    private Long id;
    private String name;
    private String description;
    private String color;
    private String status;
    private long memberCount;
    private String myRole; // MANAGER, USER, INVITED, REQUESTED, null(없음)
}
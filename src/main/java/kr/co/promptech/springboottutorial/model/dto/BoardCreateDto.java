package kr.co.promptech.springboottutorial.model.dto;

import lombok.Data;

@Data
public class BoardCreateDto {
    private String name;
    private String color;
    private String description;
}

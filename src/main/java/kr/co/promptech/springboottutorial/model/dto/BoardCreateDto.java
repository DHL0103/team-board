package kr.co.promptech.springboottutorial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardCreateDto {
    private Long id;
    private String name;
    private String color;
    private String description;
}

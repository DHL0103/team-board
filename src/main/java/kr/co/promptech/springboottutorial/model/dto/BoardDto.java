package kr.co.promptech.springboottutorial.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDto {
    @NotBlank
    @Size(max = 50)
    private String name;
    private String color;
    @Size(max = 500)
    private String description;
    private String status;
}

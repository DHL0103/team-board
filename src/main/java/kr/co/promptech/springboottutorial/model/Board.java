package kr.co.promptech.springboottutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private Long id;
    private String name;
    private String color;
    private String description;
    private String status;
}

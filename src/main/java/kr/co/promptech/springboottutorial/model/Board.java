package kr.co.promptech.springboottutorial.model;

import lombok.Data;

@Data
public class Board {
    private Long id;
    private String name;
    private String color;
    private String description;
    private String status;
}

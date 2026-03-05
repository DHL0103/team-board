package kr.co.promptech.springboottutorial.model;

import lombok.Data;

@Data
public class Member {
    private Long id;
    private String username;
    private String password;
    private String role;
}

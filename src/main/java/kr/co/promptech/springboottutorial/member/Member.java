package kr.co.promptech.springboottutorial.member;

import lombok.Data;

@Data
public class Member {
    private Long id;
    private String username;
    private String password;
    private String role;        // ROLE_USER, ROLE_ADMIN
    private Long boardId;
}

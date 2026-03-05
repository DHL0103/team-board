package kr.co.promptech.springboottutorial.model.dto;

import lombok.Data;

@Data
public class MemberCreateDto {
    String username;
    String password;
    Long boardId;
    String role;
}

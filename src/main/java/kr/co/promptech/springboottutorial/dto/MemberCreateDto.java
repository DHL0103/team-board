package kr.co.promptech.springboottutorial.dto;

import lombok.Data;

@Data
public class MemberCreateDto {
    String username;
    String password;
    Long boardId;
}

package kr.co.promptech.springboottutorial.dto;

@lombok.Data
public class MemberCreateDto {
    String username;
    String password;
    Long boardId;
}

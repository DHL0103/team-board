package kr.co.promptech.springboottutorial.model.dto;

import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private final Long id;
    private final String username;
    private final MemberRole role;

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.role = member.getRole();
    }
}

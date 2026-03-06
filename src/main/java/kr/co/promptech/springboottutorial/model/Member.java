package kr.co.promptech.springboottutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Member {
    private Long id;
    private String username; // 로그인 아이디
    private String password; // BCrypt 암호화된 비밀번호
    private String role;     // 시스템 권한 (ROLE_USER / ROLE_ADMIN)
}

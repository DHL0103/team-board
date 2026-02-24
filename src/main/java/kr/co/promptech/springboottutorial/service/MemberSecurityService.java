package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    private final MemberMapper memberMapper; // Member를 찾아올 레포지토리

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 유저 찾기
        Member member = memberMapper.findByUsername(username);

        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 2. 시큐리티가 이해할 수 있는 UserDetails 객체로 변환해서 반환
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) // DB에 저장된 암호화된 비밀번호
                .roles(member.getRole().replace("ROLE_", "")) // "ROLE_USER" -> "USER"
                .build();
    }
}

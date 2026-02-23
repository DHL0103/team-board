package kr.co.promptech.springboottutorial.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    public Member getMemberById(Long id){
        return memberMapper.selectMemberById(id);
    }

    public Member getMemberByUsername(String username) {
        return memberMapper.findByUsername(username);
    }

    public void create (String username, String password, Long boardId) {
        Member member = new Member();
        member.setUsername(username);
        member.setBoardId(boardId);
        member.setRole("ROLE_USER");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        member.setPassword(passwordEncoder.encode(password));

        this.memberMapper.save(member);
    }
}

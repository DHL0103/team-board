package kr.co.promptech.springboottutorial.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    public Member getMemberById(Long id){
        return memberMapper.selectMemberById(id);
    }
}

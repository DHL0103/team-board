package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.mapper.MemberMapper;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    public Member getMemberById(Long id) {
        return memberMapper.selectMemberById(id);
    }

    public MemberResponseDto getMemberByUsername(String username) {
        return new MemberResponseDto(memberMapper.findByUsername(username));
    }
}

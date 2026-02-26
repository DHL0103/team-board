package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.mapper.MemberMapper;
import kr.co.promptech.springboottutorial.model.dto.MemberCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void create (MemberCreateDto memberCreateDto) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberCreateDto.setPassword(passwordEncoder.encode(memberCreateDto.getPassword()));

        this.memberMapper.save(memberCreateDto);
    }

    public List<Member> getAllMemberExceptAdmin() {
        List<Member> memberList = memberMapper.getAllMemberExceptAdmin();
        return memberList;
    }
}

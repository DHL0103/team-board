package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.mapper.MemberMapper;
import kr.co.promptech.springboottutorial.model.dto.MemberPageDto;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    /**
     * @return true: 변경 성공, false: 현재 비밀번호 불일치
     */
    public boolean changePassword(Long memberId, String currentPassword, String newRawPassword) {
        Member member = memberMapper.selectMemberById(memberId);
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            return false;
        }
        memberMapper.updatePassword(memberId, passwordEncoder.encode(newRawPassword));
        return true;
    }

    /**
     * @return true: 가입 성공, false: 아이디 중복
     */
    public boolean signup(String username, String rawPassword) {
        if (memberMapper.existsByUsername(username)) {
            return false;
        }
        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(MemberRole.ROLE_USER)
                .build();
        memberMapper.insertMember(member);
        return true;
    }

    public MemberPageDto getMemberPage(String q, String filter, int page, int size) {
        int offset = page * size;
        List<Member> raw = memberMapper.getMembersPaged(q, filter, offset, size + 1);
        boolean hasMore = raw.size() > size;
        List<Member> members = hasMore ? raw.subList(0, size) : raw;
        long totalCount = memberMapper.countMembers(q, filter);
        return new MemberPageDto(members.stream().map(MemberResponseDto::new).toList(), hasMore, totalCount);
    }

    public void updateRole(Long memberId, MemberRole role) {
        if (role == MemberRole.ROLE_ADMIN) {
            throw new IllegalArgumentException("ROLE_ADMIN으로 변경할 수 없습니다.");
        }
        memberMapper.updateRole(memberId, role);
        if (role == MemberRole.ROLE_SUSPENDED) {
            String username = memberMapper.selectMemberById(memberId).getUsername();
            sessionRegistry.getAllPrincipals().stream()
                    .filter(p -> p instanceof CustomUser && ((CustomUser) p).getUsername().equals(username))
                    .flatMap(p -> sessionRegistry.getAllSessions(p, false).stream())
                    .forEach(session -> session.expireNow());
        }
    }
}

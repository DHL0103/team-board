package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.MemberBoardDto;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api")
public class AdminApiController {

    private final BoardMemberService boardMemberService;
    private final MemberService memberService;

    /**
     * @param memberId 조회할 멤버 ID
     * @return 해당 멤버가 속한 보드 목록 (MemberBoardDto JSON)
     * 멤버 상세 모달에서 소속 보드 및 역할을 lazy 로딩으로 조회
     */
    @GetMapping("/members/{memberId}/boards")
    public List<MemberBoardDto> getMemberBoards(@PathVariable Long memberId) {
        return boardMemberService.getBoardsByMemberId(memberId);
    }

    /**
     * @param memberId 대상 멤버 ID
     * @param role     변경할 역할 (ROLE_USER / ROLE_SUSPENDED)
     * ROLE_ADMIN은 변경 불가 — 요청 시 400 반환
     */
    @PostMapping("/members/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(@PathVariable Long memberId,
                                                  @RequestParam MemberRole role) {
        if (role == MemberRole.ROLE_ADMIN) {
            return ResponseEntity.badRequest().build();
        }
        memberService.updateRole(memberId, role);
        return ResponseEntity.ok().build();
    }
}

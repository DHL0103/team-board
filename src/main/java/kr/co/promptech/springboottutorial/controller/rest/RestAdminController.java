package kr.co.promptech.springboottutorial.controller.rest;

import kr.co.promptech.springboottutorial.model.dto.MemberBoardDto;
import kr.co.promptech.springboottutorial.model.dto.MemberPageDto;
import kr.co.promptech.springboottutorial.model.dto.MemberSearchParam;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class RestAdminController {

    private final BoardMemberService boardMemberService;
    private final MemberService memberService;
    private final BoardService boardService;

    @GetMapping("/members")
    public ResponseEntity<MemberPageDto> getMembers(@ModelAttribute MemberSearchParam search) {
        return ResponseEntity.ok(memberService.getMemberPage(search));
    }

    /**
     * @param memberId 조회할 멤버 ID
     * @return 해당 멤버가 속한 보드 목록 (MemberBoardDto JSON)
     */
    @GetMapping("/members/{memberId}/boards")
    public ResponseEntity<List<MemberBoardDto>> getMemberBoards(@PathVariable Long memberId) {
        return ResponseEntity.ok(boardMemberService.getBoardsByMemberId(memberId));
    }

    @PostMapping("/boards/{boardId}/status")
    public ResponseEntity<Void> updateBoardStatus(@PathVariable Long boardId,
                                                   @RequestParam BoardStatus status) {
        boardService.updateStatus(boardId, status);
        return ResponseEntity.ok().build();
    }

    /**
     * @param memberId 대상 멤버 ID
     * @param role     변경할 역할 (ROLE_USER / ROLE_SUSPENDED)
     * ROLE_ADMIN은 변경 불가 — 요청 시 400 반환
     */
    @PostMapping("/members/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(@PathVariable Long memberId,
                                                  @RequestParam MemberRole role) {
        try {
            memberService.updateRole(memberId, role);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

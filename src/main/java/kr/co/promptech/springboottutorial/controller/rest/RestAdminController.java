package kr.co.promptech.springboottutorial.controller.rest;

import kr.co.promptech.springboottutorial.model.dto.MemberBoardDto;
import kr.co.promptech.springboottutorial.model.dto.MemberPageDto;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
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
@RequestMapping("/api/admin")
public class RestAdminController {

    private final BoardMemberService boardMemberService;
    private final MemberService memberService;
    private final BoardService boardService;

    /**
     * 관리자용 멤버 목록 조회 (검색, 필터, 페이징 지원)
     * @param q      검색어 (username 기준, optional)
     * @param filter 필터 (active / suspended / all)
     * @param page   페이지 번호 (0-based)
     * @param size   페이지 크기
     * @return 멤버 목록 페이지 (MemberPageDto JSON)
     */
    @GetMapping("/members")
    public ResponseEntity<MemberPageDto> getMembers(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "active") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(memberService.getMemberPage(q, filter, page, size));
    }

    /**
     * 멤버 상세 모달에서 소속 보드 및 역할을 lazy 로딩으로 조회
     * @param memberId 조회할 멤버 ID
     * @return 해당 멤버가 속한 보드 목록 (MemberBoardDto JSON)
     */
    @GetMapping("/members/{memberId}/boards")
    public ResponseEntity<List<MemberBoardDto>> getMemberBoards(@PathVariable Long memberId) {
        return ResponseEntity.ok(boardMemberService.getBoardsByMemberId(memberId));
    }

    /**
     * 관리자용 보드 활성/비활성 상태 변경
     * @param boardId 대상 보드 ID
     * @param status  변경할 상태 (ACTIVE / INACTIVE)
     */
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

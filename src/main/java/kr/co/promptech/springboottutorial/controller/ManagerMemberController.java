package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("boards/{boardId}/manager/members")
public class ManagerMemberController {

    private final BoardMemberService boardMemberService;
    private final BoardService boardService;

    /**
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @return manager/members 뷰
     * 해당 보드의 전체 멤버 목록(MANAGER / USER / REQUESTED) 반환
     * memberList(BoardMemberResponseDto) 전달
     */
    @GetMapping
    public String membersPage(@PathVariable Long boardId, @RequestParam(required = false) String search, Model model) {
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        model.addAttribute("memberList", boardMemberService.getMembersByBoardId(boardId));
        if (search != null && !search.isBlank()) {
            model.addAttribute("search", search);
            model.addAttribute("inviteResults", boardMemberService.searchMembersForInvite(boardId, search));
        }
        return "manager/members";
    }


    /**
     * @param boardId  보드 ID
     * @param memberId 승인할 멤버 ID
     * @return manager/members 리다이렉트
     * 가입 요청(REQUESTED) 멤버를 USER로 승인
     */
    @PostMapping("/invite/{memberId}")
    public String inviteMember(@PathVariable Long boardId, @PathVariable Long memberId,
                               @RequestParam(required = false) String search) {
        boardMemberService.save(boardId, memberId, BoardRole.INVITED);
        if (search != null && !search.isBlank()) {
            return "redirect:/boards/" + boardId + "/manager/members?search=" + search;
        }
        return "redirect:/boards/" + boardId + "/manager/members";
    }

    /**
     * @param boardId  보드 ID
     * @param memberId 승인할 멤버 ID
     * @return manager/members 리다이렉트
     * 가입 요청(REQUESTED) 멤버를 USER로 승인
     */
    @PostMapping("/approve/{memberId}")
    public String approveMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.updateRole(boardId, memberId, BoardRole.USER);
        return "redirect:/boards/" + boardId + "/manager/members";
    }

    /**
     * @param boardId  보드 ID
     * @param memberId 승격할 멤버 ID
     * @return manager/members 리다이렉트
     * 멤버(USER)를 매니저(MANAGER)로 승격
     */
    @PostMapping("/promote/{memberId}")
    public String promoteMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.updateRole(boardId, memberId, BoardRole.MANAGER);
        return "redirect:/boards/" + boardId + "/manager/members";
    }

    /**
     * @param boardId  보드 ID
     * @param memberId 강등할 멤버 ID
     * @return manager/members 리다이렉트
     * 매니저(MANAGER)를 일반 멤버(USER)로 강등
     */
    @PostMapping("/demote/{memberId}")
    public String demoteMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.updateRole(boardId, memberId, BoardRole.USER);
        return "redirect:/boards/" + boardId + "/manager/members";
    }

    /**
     * @param boardId  보드 ID
     * @param memberId 내보낼 멤버 ID
     * @return manager/members 리다이렉트
     * 해당 멤버를 보드에서 제거 (가입 거절 포함)
     */
    @PostMapping("/remove/{memberId}")
    public String removeMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.delete(boardId, memberId);
        return "redirect:/boards/" + boardId + "/manager/members";
    }
}

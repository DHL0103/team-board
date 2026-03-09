package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.service.BoardMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("board/{boardId}/manager/members")
public class ManagerMemberController {

    private final BoardMemberService boardMemberService;

    /**
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @return manager/members 뷰
     * 해당 보드의 전체 멤버 목록(MANAGER / USER / REQUESTED) 반환
     * memberList(BoardMemberResponseDto) 전달
     */
    @GetMapping
    public String membersPage(@PathVariable Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        model.addAttribute("memberList", boardMemberService.getMembersByBoardId(boardId));
        return "manager/members";
    }

    @PostMapping("/approve/{memberId}")
    public String approveMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.updateRole(boardId, memberId, "USER");
        return "redirect:/board/" + boardId + "/manager/members";
    }

    @PostMapping("/promote/{memberId}")
    public String promoteMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.updateRole(boardId, memberId, "MANAGER");
        return "redirect:/board/" + boardId + "/manager/members";
    }

    @PostMapping("/demote/{memberId}")
    public String demoteMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.updateRole(boardId, memberId, "USER");
        return "redirect:/board/" + boardId + "/manager/members";
    }

    @PostMapping("/remove/{memberId}")
    public String removeMember(@PathVariable Long boardId, @PathVariable Long memberId) {
        boardMemberService.delete(boardId, memberId);
        return "redirect:/board/" + boardId + "/manager/members";
    }
}

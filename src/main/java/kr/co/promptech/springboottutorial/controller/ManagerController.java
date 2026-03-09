package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("board/{boardId}/manager")
public class ManagerController {
    private final BoardMemberService boardMemberService;

    @GetMapping("/members")
    public String membersPage(@PathVariable Long boardId, Model model){
        model.addAttribute("memberList", boardMemberService.getMembersByBoardId(boardId));
        return "board" + boardId + "manager/members";
    }
}

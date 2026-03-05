package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping
    public String getAllBoard(Model model, Principal principal) {
        Member currentMember = memberService.getMemberByUsername(principal.getName());
        if (currentMember.getRole().equals("ROLE_ADMIN")) {
            model.addAttribute("boardList", boardService.getAllBoards());
        } else {
            model.addAttribute("boardList", boardService.getBoardsByMemberId(currentMember.getId()));
        }
        return "main_page";
    }

}

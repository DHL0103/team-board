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

    //로그인 이후 들어오는 메인페이지 로드
    @GetMapping
    public String getAllBoard(Model model, Principal principal) {
        Member currentMember = memberService.getMemberByUsername(principal.getName());
        //유저의 시스템 레벨에 따라서 보여주는 보드 목록 구분
        if (currentMember.getRole().equals("ROLE_ADMIN")) {
            //시스템 레벨 admin이라면 모든 보드 반환
            model.addAttribute("boardList", boardService.getAllBoards());
        } else {
            //시스템 레벨 user라면 board_members 테이블을 이용하여 속해있는 보드만 조회
            model.addAttribute("boardList", boardService.getBoardsByMemberId(currentMember.getId()));
        }
        return "main_page";
    }

}

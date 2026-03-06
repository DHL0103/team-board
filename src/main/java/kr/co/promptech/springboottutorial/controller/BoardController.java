package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;
    private final BoardMemberService boardMemberService;
    private final PostService postService;
    /**
     * @param model     뷰에 전달할 데이터 컨테이너
     * @param principal 현재 로그인한 사용자 정보
     * @return 메인 페이지 뷰 이름 (main_page)
     * ADMIN이면 전체 보드 목록, 일반 USER면 소속 보드 목록만 반환
     * model에 담기는 boardList는 BoardResponseDto
     */
    @GetMapping
    public String getAllBoard(Model model, Principal principal) {
        MemberResponseDto currentMember = memberService.getMemberByUsername(principal.getName());
        //유저의 시스템 레벨에 따라서 보여주는 보드 목록 구분
        if (currentMember.getRole().equals("ROLE_ADMIN")) {
            //시스템 레벨 admin이라면 모든 보드 반환
            model.addAttribute("boardList", boardService.getAllBoardDtos());
        } else {
            //시스템 레벨 user라면 board_members 테이블을 이용하여 속해있는 보드만 조회
            model.addAttribute("boardList", boardService.getBoardsByMemberIdDtos(currentMember.getId()));
        }
        return "main_page";
    }

    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId, Model model, Principal principal) {
        MemberResponseDto currentMember = memberService.getMemberByUsername(principal.getName());
        boolean isBoardMember = boardMemberService.isMember(boardId, currentMember.getId());
        BoardResponseDto board = boardService.getBoardDtoById(boardId);
        model.addAttribute("board", board);
        if (isBoardMember) {
            model.addAttribute("postList", postService.getPostsByBoardId(boardId));
            return "board/detail";
        } else {
            return "board/request";
        }
    }

}

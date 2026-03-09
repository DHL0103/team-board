package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;
    private final PostService postService;
    private final BoardMemberService boardMemberService;
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

    /**
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @return board/detail 뷰
     * 인터셉터(BoardAuthInterceptor)에서 비멤버를 /board/{boardId}/request로 리다이렉트하므로
     * 이 메서드에 도달한 사용자는 항상 보드 멤버임이 보장됨
     * board(BoardResponseDto), postList(PostResponseDto) 전달
     */
    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId, Model model) {
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        model.addAttribute("postList", postService.getPostDtosByBoardId(boardId));
        return "board/detail";
    }

    /**
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @return board/request 뷰
     * 인터셉터가 비멤버를 이 URL로 리다이렉트함
     * board(BoardResponseDto) 전달
     */
    @GetMapping("/{boardId}/request")
    public String boardRequest(@PathVariable Long boardId, Model model, Principal principal) {
        MemberResponseDto member = memberService.getMemberByUsername(principal.getName());
        if (boardMemberService.isMember(boardId, member.getId()) || member.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/board/" + boardId;
        }
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        model.addAttribute("isRequested", boardMemberService.isRequested(boardId, member.getId()));
        return "board/request";
    }

    /**
     * @param boardId 조회할 보드 ID
     * @param principal   뷰에 전달할 데이터 컨테이너
     * @return board/request 페이지로 리다이렉트
     * board_members 테이블에 role을 requested로 저장
     */
    @PostMapping("/{boardId}/request")
    public String boardRequestPost(@PathVariable Long boardId, Principal principal) {
        boardMemberService.save(boardId, memberService.getMemberByUsername(principal.getName()).getId(), "REQUESTED");
        return "redirect:/board/" + boardId + "/request";
    }

    /**
     * @param boardId 조회할 보드 ID
     * @param status  필터링할 상태값 (PROGRESS / REQUESTED / COMPLETED)
     * @param model   뷰에 전달할 데이터 컨테이너
     * @return board/post_list 뷰
     * PROGRESS는 REJECTED 포스트도 함께 포함하여 반환
     * board(BoardResponseDto), postList(PostResponseDto) 전달
     */
    @GetMapping("/{boardId}/post_list")
    public String postList(@PathVariable Long boardId, @RequestParam String status, Model model) {
        BoardResponseDto board = boardService.getBoardDtoById(boardId);
        model.addAttribute("board", board);
        model.addAttribute("status", status);
        model.addAttribute("postList", postService.getPostDtosByBoardIdAndStatus(boardId, status));
        return "board/post_list";
    }

    /**
     * @param boardCreateDto 생성할 보드 정보 (name, color, description)
     * @param principal      현재 로그인한 사용자 정보
     * @return 메인 페이지로 리다이렉트
     * 보드 생성 후 생성자를 해당 보드의 MANAGER로 board_members에 등록
     */
    @PostMapping("/create")
    public String createBoard(@ModelAttribute BoardCreateDto boardCreateDto, Principal principal) {
        Long boardId = boardService.createBoard(boardCreateDto);
        MemberResponseDto member = memberService.getMemberByUsername(principal.getName());
        boardMemberService.save(boardId, member.getId(), "MANAGER");
        return "redirect:/board";
    }

}

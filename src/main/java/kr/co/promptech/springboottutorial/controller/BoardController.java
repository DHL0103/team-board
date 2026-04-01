package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.BoardDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.PostMemberService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final BoardMemberService boardMemberService;
    private final PostMemberService postMemberService;

    /**
     * @return main_page 뷰
     */
    @GetMapping
    public String getMainPage() {
        return "main_page";
    }

    /**
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @param user    현재 로그인한 사용자 정보
     * @return board/detail 뷰
     * 인터셉터(BoardAuthInterceptor)에서 비멤버를 /board/{boardId}/request로 리다이렉트하므로
     * 이 메서드에 도달한 사용자는 항상 보드 멤버임이 보장됨
     * board(BoardResponseDto), postList(PostResponseDto) 전달
     */
    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId, Model model, @AuthenticationPrincipal CustomUser user) {
        model.addAttribute("board", boardService.getBoardDetail(boardId, user));
        model.addAttribute("myPostIds", postMemberService.getMyPostIds(boardId, user.getId()));
        return "board/detail";
    }

    /**
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @param user    현재 로그인한 사용자 정보
     * @return board/request 뷰
     * 인터셉터가 비멤버를 이 URL로 리다이렉트함
     * 해당 board 소속 멤버 혹은 시스템 레벨 관리자인 경우 보드 상세 페이지로 리다이렉트
     * board(BoardResponseDto) 전달
     */
    @GetMapping("/{boardId}/request")
    public String boardRequest(@PathVariable Long boardId, Model model, @AuthenticationPrincipal CustomUser user) {
        if (boardMemberService.isMember(boardId, user.getId()) || MemberRole.ROLE_ADMIN == user.getRole()) {
            return "redirect:/board/" + boardId;
        }
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        model.addAttribute("isRequested", boardMemberService.isRequested(boardId, user.getId()));
        model.addAttribute("isInvited", boardMemberService.isInvited(boardId, user.getId()));
        return "board/request";
    }

    /**
     * 비활성(INACTIVE) 보드 안내 페이지 렌더링
     * ACTIVE 보드이거나 시스템 관리자 또는 보드 매니저인 경우 보드 상세 페이지로 리다이렉트
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @param user    현재 로그인한 사용자 정보
     * @return board/inactive 뷰 또는 board 상세 페이지로 리다이렉트
     */
    @GetMapping("/{boardId}/inactive")
    public String boardInactive(@PathVariable Long boardId, Model model, @AuthenticationPrincipal CustomUser user) {
        BoardResponseDto board = boardService.getBoardDtoById(boardId);
        if (board == null || BoardStatus.ACTIVE.name().equals(board.getStatus())
                || MemberRole.ROLE_ADMIN == user.getRole()
                || boardMemberService.isManager(boardId, user.getId())) {
            return "redirect:/board/" + boardId;
        }
        model.addAttribute("board", board);
        return "board/inactive";
    }

    /**
     * board_members 테이블에 role을 REQUESTED로 저장 (보드 가입 요청)
     * @param boardId 가입 요청할 보드 ID
     * @param user    현재 로그인한 사용자 정보
     * @return board/request 페이지로 리다이렉트
     */
    @PostMapping("/{boardId}/request")
    public String boardRequestPost(@PathVariable Long boardId, @AuthenticationPrincipal CustomUser user) {
        boardMemberService.save(boardId, user.getId(), BoardRole.REQUESTED);
        return "redirect:/board/" + boardId + "/request";
    }

    /**
     * 게시글 목록은 REST API(/api/board/{boardId}/posts)로 동적 로드
     * board(BoardResponseDto), status, myPostIds 전달
     * @param boardId 조회할 보드 ID
     * @param status  필터링할 상태값 (PROGRESS / REQUESTED / APPROVED)
     * @param model   뷰에 전달할 데이터 컨테이너
     * @param user    현재 로그인한 사용자 정보
     * @return board/post_list 뷰
     */
    @GetMapping("/{boardId}/post_list")
    public String postList(@PathVariable Long boardId, @RequestParam String status, Model model, @AuthenticationPrincipal CustomUser user) {
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        model.addAttribute("status", status);
        model.addAttribute("myPostIds", postMemberService.getMyPostIds(boardId, user.getId()));
        return "board/post_list";
    }

    /**
     * @param boardCreateDto 생성할 보드 정보 (name, color, description)
     * @param user           현재 로그인한 사용자 정보
     * @return 메인 페이지로 리다이렉트
     * 보드 생성 후 생성자를 해당 보드의 MANAGER로 board_members에 등록
     */
    @PostMapping("/create")
    public String createBoard(@Valid @ModelAttribute BoardDto boardCreateDto, @AuthenticationPrincipal CustomUser user) {
        Long boardId = boardService.createBoard(boardCreateDto);
        boardMemberService.save(boardId, user.getId(), BoardRole.MANAGER);
        return "redirect:/board";
    }

    /**
     * 현재 사용자를 보드에서 탈퇴 처리
     * @param boardId 탈퇴할 보드 ID
     * @param user    현재 로그인한 사용자 정보
     * @return 메인 페이지로 리다이렉트, 유일 매니저인 경우 보드 상세로 리다이렉트(error=last_manager)
     */
    @PostMapping("/{boardId}/leave")
    public String leaveBoard(@PathVariable Long boardId, @AuthenticationPrincipal CustomUser user) {
        try {
            boardMemberService.leaveBoard(boardId, user.getId());
        } catch (IllegalStateException e) {
            return "redirect:/board/" + boardId + "?error=last_manager";
        }
        return "redirect:/board";
    }

    /**
     * @return board/search 보드 탐색 페이지 반환
     */
    @GetMapping("/search")
    public String searchBoards() {
        return "board/search";
    }
}

package kr.co.promptech.springboottutorial.controller.rest;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.BoardPageDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.model.dto.BoardSearchParam;
import kr.co.promptech.springboottutorial.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class RestBoardController {

    private final BoardService boardService;

    /**
     * 상태별 보드 목록 조회
     * @param status    조회할 보드 상태
     * @param boardName 보드명 검색어 (기본값: 빈 문자열)
     * @param user      현재 로그인 사용자
     * @return 조건에 맞는 보드 목록 (BoardResponseDto JSON)
     */
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getBoards(
            @RequestParam String status,
            @RequestParam(defaultValue = "") String boardName,
            @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(boardService.getBoardDtosByStatus(status, boardName, user));
    }

    /**
     * 보드 탐색 페이지에서 사용하는 검색/필터/정렬/페이징 API
     * @param search 검색/필터/정렬/페이징 조건 (BoardSearchParam)
     * @param user   현재 로그인 사용자
     * @return 검색 조건에 맞는 보드 페이지 (BoardPageDto JSON)
     */
    @GetMapping("/search")
    public ResponseEntity<BoardPageDto> searchBoards(
            @ModelAttribute BoardSearchParam search,
            @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(boardService.getBoardPageForSearch(search, user.getId()));
    }
}

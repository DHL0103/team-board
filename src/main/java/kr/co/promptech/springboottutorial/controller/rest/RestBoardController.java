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
@RequestMapping("/api/board")
public class RestBoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getBoards(
            @RequestParam String status,
            @RequestParam(defaultValue = "") String boardName,
            @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(boardService.getBoardDtosByStatus(status, boardName, user));
    }

    @GetMapping("/search")
    public ResponseEntity<BoardPageDto> searchBoards(
            @ModelAttribute BoardSearchParam search,
            @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(boardService.getBoardPageForSearch(search, user.getId()));
    }
}

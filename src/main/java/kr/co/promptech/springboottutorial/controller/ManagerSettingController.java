package kr.co.promptech.springboottutorial.controller;

import jakarta.validation.Valid;
import kr.co.promptech.springboottutorial.model.dto.BoardDto;
import kr.co.promptech.springboottutorial.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("boards/{boardId}/manager/settings")
@RequiredArgsConstructor
public class ManagerSettingController {
    private final BoardService boardService;

    /**
     * 보드 설정 페이지 렌더링, board(BoardResponseDto) 전달
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @return manager/settings 뷰
     */
    @GetMapping
    public String settingsPage(@PathVariable Long boardId, Model model) {
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        return "manager/settings";
    }

    /**
     * 보드 설정(이름, 색상, 설명) 수정
     * @param boardId       수정할 보드 ID
     * @param boardCreateDto 수정할 보드 정보 (name, color, description)
     * @return manager/settings 리다이렉트
     */
    @PostMapping
    public String updateSettings(@PathVariable Long boardId, @Valid @ModelAttribute BoardDto boardCreateDto) {
        boardService.updateBoard(boardId, boardCreateDto);
        return "redirect:/boards/" + boardId + "/manager/settings";
    }
}

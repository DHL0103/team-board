package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.BoardUpdateDto;
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
@RequestMapping("board/{boardId}/manager/settings")
@RequiredArgsConstructor
public class ManagerSettingController {
    private final BoardService boardService;

    @GetMapping
    public String settingsPage(@PathVariable Long boardId, Model model) {
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        return "manager/settings";
    }

    @PostMapping
    public String updateSettings(@PathVariable Long boardId, @ModelAttribute BoardUpdateDto boardUpdateDto) {
        boardService.updateBoard(boardId, boardUpdateDto);
        return "redirect:/board/" + boardId + "/manager/settings";
    }
}

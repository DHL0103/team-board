package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("board/{boardId}/manager/settings")
@RequiredArgsConstructor
public class ManagerSettingController {
    private final BoardService boardService;

    @GetMapping
    public String settingsPage(Model model,@PathVariable Long boardId) {
        model.addAttribute("board", boardService.getBoardDtoById(boardId));
        return "manager/settings";
    }   
}

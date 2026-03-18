package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/boards")
public class AdminBoardController {
    private final BoardService boardService;

    @GetMapping
    public String adminBoardPage(Model model){
        List<BoardResponseDto> boardResponseDtoList = boardService.getAllBoardDtos();
        model.addAttribute("boardResponseDtoList", boardResponseDtoList);
        return "admin/boards";
    }
}

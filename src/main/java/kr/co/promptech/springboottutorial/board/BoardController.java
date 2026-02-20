package kr.co.promptech.springboottutorial.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public String getAllBoard(Model model) {
        List<Board> boardList = boardService.getAllBoard();
        model.addAttribute("boardList",boardList);
        return "main_page";
    }


}

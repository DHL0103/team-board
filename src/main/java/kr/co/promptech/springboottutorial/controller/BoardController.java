package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final PostService postService;
    private final BoardService boardService;

    //처음 메인페이지 로드 (모든 포스트,보드 가져옴)
    @GetMapping
    public String getAllPost(Model model) {
        //APPROVED된것 제외하고 가져오기
        List<Post> postList = postService.getCurrentPost();
        List<Board> boardList = boardService.getAllBoards();
        model.addAttribute("postList", postList);
        model.addAttribute("boardList", boardList);
        return "main_page";
    }


}

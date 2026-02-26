package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.mapper.PostMapper;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final PostService postService;
    private final BoardService boardService;

    @GetMapping("/request")
    public String requestPage(Model model){
        List<Post> postList = postService.getRequestedPost();
        model.addAttribute("postList", postList);
        return "admin/request";
    }

    @GetMapping("/members")
    public String memberPage(){


        return "admin/members";
    }
    @GetMapping("/boards")
    public String boardsPage(Model model){
        List<Board> boardList = boardService.getAllBoards();
        model.addAttribute("boardList", boardList);
        return "admin/boards";
    }

    @PostMapping("/board/create")
    public String createBoard(@ModelAttribute BoardCreateDto boardCreateDto){
        boardService.createBoard(boardCreateDto);

        return "redirect:/admin/boards";
    }


//    @PostMapping("/board/update/{id}")
//    public String updateBoard(@PathVariable Long id,@RequestBody BoardCreateDto boardCreateDto){
//
//
//    }




}

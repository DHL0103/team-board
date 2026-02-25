package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.mapper.PostMapper;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final PostService postService;

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
    public String boardsPage(){


        return "admin/boards";
    }
}

package kr.co.promptech.springboottutorial.board;

import kr.co.promptech.springboottutorial.post.Post;
import kr.co.promptech.springboottutorial.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final PostService postService;

    //처음 메인페이지 로드 (모든 포스트 가져옴)
    @GetMapping("/board")
    public String getAllBoard(Model model) {
        List<Post> postList = postService.getAllPost();
        model.addAttribute("postList", postList);
        return "main_page";
    }
}

package kr.co.promptech.springboottutorial.post;

import kr.co.promptech.springboottutorial.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    @GetMapping("/{id}")
    public String getPostDetailPage(@PathVariable Long id, Model model) {
        // 1. 게시글 상세 데이터 가져오기
        Post post = postService.getPostById(id);

        // 2. 해당 게시글에 달린 댓글 리스트 가져오기
        //List<Comment> comments = commentService.getCommentsByPostId(id);

        // 3. Model에 두 데이터 모두 담기
        model.addAttribute("post", post);
        //model.addAttribute("commentList", comments);

        return "post_detail";
    }

}

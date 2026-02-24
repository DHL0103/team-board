package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

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

    @PostMapping("/create")
    public String createPost(PostCreateDto postCreateDto, Model model, Principal principal) {
        postService.createPost(
                postCreateDto.getBoardId(),
                postCreateDto.getTitle(),
                postCreateDto.getContent(),
                postCreateDto.getDueDate(),
                principal.getName()
        );
        return "redirect:/board";

    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Principal principal) {
        Post post = postService.getPostById(id);
        Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();

        if (!post.getMemberId().equals(currentMemberId)) {
            // 본인이 아니면 삭제 거부 (에러 페이지나 메시지 처리)
            return "redirect:/post/" + id + "?error=unauthorized";
        }

        postService.deletePost(post);

        return "redirect:/board";
    }

}

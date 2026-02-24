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
    public String getPostDetailPage(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);

        if (principal != null) {
            Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();
            model.addAttribute("isOwner", post.getMemberId().equals(currentMemberId));
        } else {
            model.addAttribute("isOwner", false);
        }

        return "post/detail";
    }

    @PostMapping("/create")
    public String createPost(PostCreateDto postCreateDto, Model model, Principal principal) {
        postService.createPost(postCreateDto, principal.getName());
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

    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long id,PostCreateDto postCreateDto, Principal principal) {
        Post post = postService.getPostById(id);
        Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();

        if (!post.getMemberId().equals(currentMemberId)) {
            // 본인이 아니면 수정 거부 (에러 페이지나 메시지 처리)
            return "redirect:/post/" + id + "?error=unauthorized";
        }

        postService.updatePost(post, postCreateDto);
        return "redirect:/post/" + id;
    }

}

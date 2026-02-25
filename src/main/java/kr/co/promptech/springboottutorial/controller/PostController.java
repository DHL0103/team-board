package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.service.PostFileService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final MemberService memberService;
    private final PostFileService postFileService;

    @GetMapping("/{id}")
    public String getPostDetailPage(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("postFiles", postFileService.getFilesByPostId(id));

        if (principal != null) {
            Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();
            model.addAttribute("isOwner", post.getMemberId().equals(currentMemberId));
        } else {
            model.addAttribute("isOwner", false);
        }

        return "post/detail";
    }

    @PostMapping("/create")
    public String createPost(PostCreateDto postCreateDto,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             Principal principal) {
        Long postId = postService.createPost(postCreateDto, principal.getName());
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        postFileService.saveFile(file, postId);
                    } catch (Exception e) {
                        // 파일 저장 실패해도 게시글은 유지
                        log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
                    }
                }
            }
        }
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
    public String updatePost(@PathVariable Long id,
                             PostCreateDto postCreateDto,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                             Principal principal) {
        Post post = postService.getPostById(id);
        Long currentMemberId = memberService.getMemberByUsername(principal.getName()).getId();

        if (!post.getMemberId().equals(currentMemberId)) {
            return "redirect:/post/" + id + "?error=unauthorized";
        }

        postService.updatePost(post, postCreateDto);

        if (deleteFileIds != null) {
            postFileService.deleteFiles(deleteFileIds);
        }

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        postFileService.saveFile(file, id);
                    } catch (Exception e) {
                        log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
                    }
                }
            }
        }

        return "redirect:/post/" + id;
    }

}

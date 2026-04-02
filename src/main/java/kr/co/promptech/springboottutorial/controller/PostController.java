package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.model.dto.PostDto;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("boards/{boardId}/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public String getPostDetailPage(@PathVariable Long postId, Model model, @AuthenticationPrincipal CustomUser user) {
        model.addAttribute("post", postService.getPostDetail(postId, user));
        return "post/detail";
    }

    /**
     * @param postCreateDto 게시글 생성 데이터 (제목, 내용, 보드ID 등)
     * @param files         첨부파일 목록 (optional)
     * @param user          현재 로그인한 사용자 정보
     * @return 보드 상세 페이지로 리다이렉트
     * 게시글 생성 후 첨부파일 저장. 파일 저장 실패 시 게시글은 유지
     */
    @PostMapping("/create")
    public String createPost(@Valid PostDto postCreateDto,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             @AuthenticationPrincipal CustomUser user) {
        postService.createPost(postCreateDto, user.getId(), files);
        return "redirect:/boards/{boardId}";
    }

    /**
     * @param id 삭제할 게시글 PK
     * @return 보드 상세 페이지로 리다이렉트
     * 권한 검증은 PostMemberAuthInterceptor에서 처리
     */
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long boardId, @PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/boards/" + boardId;
    }

    /**
     * @param id            수정할 게시글 PK
     * @param postCreateDto 수정할 게시글 데이터
     * @param files         새로 추가할 첨부파일 목록 (optional)
     * @param deleteFileIds 삭제할 첨부파일 ID 목록 (optional)
     * @return 게시글 상세 페이지로 리다이렉트
     * 권한 검증은 PostMemberAuthInterceptor에서 처리
     */
    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long boardId,
                             @PathVariable Long id,
                             @Valid PostDto postCreateDto,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds) {
        postService.updatePost(id, postCreateDto, files, deleteFileIds);
        return "redirect:/boards/" + boardId + "/posts/" + id;
    }

    /**
     * @param id 승인 요청할 게시글 PK
     * @return 게시글 상세 페이지로 리다이렉트
     * 권한 검증은 PostMemberAuthInterceptor에서 처리
     */
    @PostMapping("/request/{id}")
    public String requestPost(@PathVariable Long boardId, @PathVariable Long id) {
        postService.updateStatus(id, PostStatus.REQUESTED);
        return "redirect:/boards/" + boardId + "/posts/" + id;
    }
}

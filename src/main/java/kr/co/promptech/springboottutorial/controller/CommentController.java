package kr.co.promptech.springboottutorial.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.CommentDto;
import kr.co.promptech.springboottutorial.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}/post/{postId}/comment")
public class CommentController {

    private final CommentService commentService;

    /**
     * @param boardId 보드 ID
     * @param postId  댓글이 달릴 게시글 ID
     * @param dto     댓글 데이터 (postId, content)
     * @param user    현재 로그인한 사용자 정보
     * @return 게시글 상세 페이지 댓글 섹션으로 리다이렉트
     */
    @PostMapping
    public String addComment(@PathVariable Long boardId,
                             @PathVariable Long postId,
                             @Valid @ModelAttribute CommentDto dto,
                             @AuthenticationPrincipal CustomUser user) {
        commentService.save(user.getId(), dto);
        return "redirect:/board/" + boardId + "/post/" + postId + "#comment-section";
    }

    /**
     * @param boardId   보드 ID
     * @param postId    게시글 ID
     * @param commentId 수정할 댓글 ID
     * @param content   수정할 댓글 내용
     * @param user      현재 로그인한 사용자 (본인 댓글만 수정 가능)
     * @return 게시글 상세 페이지 댓글 섹션으로 리다이렉트
     */
    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable Long boardId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId,
                              @RequestParam @NotBlank @Size(max = 256) String content,
                              @AuthenticationPrincipal CustomUser user) {
        commentService.update(commentId, user.getId(), content);
        return "redirect:/board/" + boardId + "/post/" + postId + "#comment-section";
    }

    /**
     * @param boardId   보드 ID
     * @param postId    게시글 ID
     * @param commentId 삭제할 댓글 ID
     * @param user      현재 로그인한 사용자 (본인 댓글 또는 매니저/관리자만 삭제 가능)
     * @return 게시글 상세 페이지 댓글 섹션으로 리다이렉트
     */
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long boardId,
                                @PathVariable Long postId,
                                @PathVariable Long commentId,
                                @AuthenticationPrincipal CustomUser user) {
        commentService.delete(commentId, user.getId(), user.getRole(), boardId);
        return "redirect:/board/" + boardId + "/post/" + postId + "#comment-section";
    }
}
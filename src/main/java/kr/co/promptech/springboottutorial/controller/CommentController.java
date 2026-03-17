package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.CommentCreateDto;
import kr.co.promptech.springboottutorial.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}/post/{postId}/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public String addComment(@PathVariable Long boardId,
                             @PathVariable Long postId,
                             @ModelAttribute CommentCreateDto dto,
                             @AuthenticationPrincipal CustomUser user) {
        commentService.save(user.getId(), dto);
        return "redirect:/board/" + boardId + "/post/" + postId + "#comment-section";
    }

    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable Long boardId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId,
                              @RequestParam String content,
                              @AuthenticationPrincipal CustomUser user) {
        commentService.update(commentId, user.getId(), content);
        return "redirect:/board/" + boardId + "/post/" + postId + "#comment-section";
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long boardId,
                                @PathVariable Long postId,
                                @PathVariable Long commentId,
                                @AuthenticationPrincipal CustomUser user) {
        commentService.delete(commentId, user.getId(), user.getRole(), boardId);
        return "redirect:/board/" + boardId + "/post/" + postId + "#comment-section";
    }
}
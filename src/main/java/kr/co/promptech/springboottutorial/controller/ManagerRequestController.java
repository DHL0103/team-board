package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.service.PostRejectionService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("board/{boardId}/manager/requests")
public class ManagerRequestController {

    private final PostService postService;
    private final PostRejectionService postRejectionService;

    /**
     * @param boardId 조회할 보드 ID
     * @param model   뷰에 전달할 데이터 컨테이너
     * @return manager/requests 뷰
     * 해당 보드의 승인 요청(REQUESTED) 상태 포스트 목록 반환
     * requestList(PostResponseDto) 전달
     */
    @GetMapping
    public String requestsPage(@PathVariable Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        model.addAttribute("requestList", postService.getRequestedPostDtosByBoardId(boardId));
        return "manager/requests";
    }

    /**
     * @param boardId 보드 ID
     * @param postId  승인할 포스트 ID
     * @return manager/requests 리다이렉트
     * 포스트 상태를 APPROVED로 변경
     */
    @PostMapping("/approve/{postId}")
    public String approve(@PathVariable Long boardId, @PathVariable Long postId) {
        postService.updateStatus(postId, "APPROVED");
        return "redirect:/board/" + boardId + "/manager/requests";
    }

    /**
     * @param boardId   보드 ID
     * @param postId    반려할 포스트 ID
     * @param reason    반려 사유
     * @param user      현재 로그인한 사용자 (반려자 기록용)
     * @return manager/requests 리다이렉트
     * 포스트 상태를 REJECTED로 변경하고 반려 사유 저장
     */
    @PostMapping("/reject/{postId}")
    public String reject(@PathVariable Long boardId, @PathVariable Long postId, @RequestParam String reason, @AuthenticationPrincipal CustomUser user) {
        postService.updateStatus(postId, "REJECTED");
        postRejectionService.save(postId, reason, user.getId());
        return "redirect:/board/" + boardId + "/manager/requests";
    }
}

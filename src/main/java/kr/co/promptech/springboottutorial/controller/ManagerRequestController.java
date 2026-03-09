package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("board/{boardId}/manager/requests")
public class ManagerRequestController {

    private final PostService postService;

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
}

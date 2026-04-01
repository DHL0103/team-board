package kr.co.promptech.springboottutorial.controller.rest;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.PostPageDto;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class RestPostController {

    private final PostService postService;

    /**
     * 보드별 게시글 목록 조회 (검색, 필터, 정렬, 페이징 지원)
     * @param boardId  조회할 보드 ID
     * @param status   필터링할 상태값
     * @param q        검색어 (optional)
     * @param sort     정렬 기준 (newest 등)
     * @param mineOnly 내 게시글만 조회 여부
     * @param page     페이지 번호 (0-based)
     * @param size     페이지 크기
     * @param user     현재 로그인한 사용자 정보
     * @return 조건에 맞는 게시글 페이지 (PostPageDto JSON)
     */
    @GetMapping("/{boardId}/posts")
    public ResponseEntity<PostPageDto> getPosts(
            @PathVariable Long boardId,
            @RequestParam String status,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "false") boolean mineOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(postService.getPostPage(boardId, status, q, sort, mineOnly, user.getId(), page, size));
    }
}

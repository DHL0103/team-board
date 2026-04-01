package kr.co.promptech.springboottutorial.controller.rest;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.PageSearchDto;
import kr.co.promptech.springboottutorial.model.dto.PostPageDto;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
     * @param search   검색/필터/정렬/페이징 조건 (PageSearchDto)
     * @param mineOnly 내 게시글만 조회 여부
     * @param user     현재 로그인한 사용자 정보
     * @return 조건에 맞는 게시글 페이지 (PostPageDto JSON)
     */
    @GetMapping("/{boardId}/posts")
    public ResponseEntity<PostPageDto> getPosts(
            @PathVariable Long boardId,
            @ModelAttribute PageSearchDto search,
            @RequestParam(defaultValue = "false") boolean mineOnly,
            @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(postService.getPostPage(boardId, search, mineOnly, user.getId()));
    }
}

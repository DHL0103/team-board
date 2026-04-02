package kr.co.promptech.springboottutorial.controller.rest;

import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.PostSearchParam;
import kr.co.promptech.springboottutorial.model.dto.PostPageDto;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class RestPostController {

    private final PostService postService;

    @GetMapping("/{boardId}/posts")
    public ResponseEntity<PostPageDto> getPosts(
            @PathVariable Long boardId,
            @ModelAttribute PostSearchParam search,
            @AuthenticationPrincipal CustomUser user) {
        return ResponseEntity.ok(postService.getPostPage(boardId, search, user.getId()));
    }
}

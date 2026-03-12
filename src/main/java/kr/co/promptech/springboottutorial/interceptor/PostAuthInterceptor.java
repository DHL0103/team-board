package kr.co.promptech.springboottutorial.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PostAuthInterceptor implements HandlerInterceptor {

    private final PostService postService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long boardId = Long.parseLong(pathVariables.get("boardId"));

        String postIdStr = pathVariables.get("postId");
        if (postIdStr == null) {
            return true;
        }
        Long postId;
        try {
            postId = Long.parseLong(postIdStr);
        } catch (NumberFormatException e) {
            // create, delete, update 등 비숫자 세그먼트는 통과
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUser user)) {
            response.sendRedirect("/member/login");
            return false;
        }

        // 시스템 레벨 관리자는 통과
        if (MemberRole.ROLE_ADMIN == user.getRole()) {
            return true;
        }

        // post가 해당 board 소속인지 확인 (IDOR 방어)
        Post post = postService.getPostById(postId);
        if (post == null || !post.getBoardId().equals(boardId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}

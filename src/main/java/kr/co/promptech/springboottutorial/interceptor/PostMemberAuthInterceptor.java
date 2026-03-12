package kr.co.promptech.springboottutorial.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
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
public class PostMemberAuthInterceptor implements HandlerInterceptor {

    private final BoardMemberService boardMemberService;
    private final PostService postService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long boardId = Long.parseLong(pathVariables.get("boardId"));
        Long postId = Long.parseLong(pathVariables.get("id"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUser user)) {
            response.sendRedirect("/member/login");
            return false;
        }

        if (MemberRole.ROLE_ADMIN == user.getRole()) {
            return true;
        }

        if (boardMemberService.isManager(boardId, user.getId())) {
            return true;
        }

        Post post = postService.getPostById(postId);
        if (PostStatus.APPROVED.name().equals(post.getStatus())
                || PostStatus.REQUESTED.name().equals(post.getStatus())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        if (postService.isAssignee(postId, user.getId())) {
            return true;
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}

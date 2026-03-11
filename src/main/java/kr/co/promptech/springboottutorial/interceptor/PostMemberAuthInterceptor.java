package kr.co.promptech.springboottutorial.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PostMemberAuthInterceptor implements HandlerInterceptor {

    private final BoardMemberService boardMemberService;
    private final PostService postService;
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long boardId = Long.parseLong(pathVariables.get("boardId"));
        Long postId = Long.parseLong(pathVariables.get("id"));

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            response.sendRedirect("/member/login");
            return false;
        }
        MemberResponseDto member = memberService.getMemberByUsername(principal.getName());

        if (MemberRole.ROLE_ADMIN == member.getRole()) {
            return true;
        }

        if (boardMemberService.isManager(boardId, member.getId())) {
            return true;
        }

        Post post = postService.getPostById(postId);
        if (PostStatus.APPROVED.name().equals(post.getStatus())
                || PostStatus.REQUESTED.name().equals(post.getStatus())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        if (postService.isAssignee(postId, member.getId())) {
            return true;
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}

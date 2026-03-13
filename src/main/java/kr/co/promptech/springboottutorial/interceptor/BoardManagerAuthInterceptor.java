package kr.co.promptech.springboottutorial.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BoardManagerAuthInterceptor implements HandlerInterceptor {

    private final BoardMemberService boardMemberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long boardId = Long.parseLong(pathVariables.get("boardId"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUser user)) {
            response.sendRedirect("/member/login");
            return false;
        }

        // 시스템 레벨 관리자는 통과
        if (MemberRole.ROLE_ADMIN == user.getRole()) {
            return true;
        }

        // 보드 레벨 매니저 여부 확인
        if (!boardMemberService.isManager(boardId, user.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}

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
public class BoardAuthInterceptor implements HandlerInterceptor {

    private final BoardMemberService boardMemberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. URL에서 boardId 추출 (예: /board/5 -> 5)
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long boardId = Long.parseLong(pathVariables.get("boardId"));

        // 2. 로그인 사용자 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUser user)) {
            response.sendRedirect("/member/login");
            return false;
        }

        //시스템 레벨 관리자는 통과
        if (MemberRole.ROLE_ADMIN == user.getRole()) {
            return true;
        }

        // 3. 멤버 여부 확인
        if (!boardMemberService.isMember(boardId, user.getId())) {
            // 멤버가 아니면 request 페이지로 강제 이동시키고 컨트롤러 진입 막기
            response.sendRedirect("/board/" + boardId + "/request");
            return false;
        }

        return true; // 멤버면 컨트롤러로 통과!
    }
}

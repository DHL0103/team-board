package kr.co.promptech.springboottutorial.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.PasswordChangeDto;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final BoardMemberService boardMemberService;
    private final PostMemberService postMemberService;

    @GetMapping
    public String myPage(@AuthenticationPrincipal CustomUser user,
                         @ModelAttribute PasswordChangeDto passwordChangeDto,
                         Model model) {
        populateMyPageModel(model, user);
        return "mypage";
    }

    /**
     * 비밀번호 변경 — 성공 시 세션 만료 후 로그인 페이지로 이동
     */
    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal CustomUser user,
                                 @Valid @ModelAttribute PasswordChangeDto passwordChangeDto,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            populateMyPageModel(model, user);
            return "mypage";
        }
        if (!memberService.changePassword(user.getId(), passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword())) {
            bindingResult.rejectValue("currentPassword", "wrong", "현재 비밀번호가 올바르지 않습니다.");
            populateMyPageModel(model, user);
            return "mypage";
        }
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/member/login?passwordChanged=true";
    }

    private void populateMyPageModel(Model model, CustomUser user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("invitedBoards", boardMemberService.getInvitedBoards(user.getId()));
        model.addAttribute("assignedPosts", postMemberService.getAssignedPosts(user.getId()));
    }

    /**
     * 초대 수락 — INVITED → USER
     */
    @PostMapping("/boards/{boardId}/accept")
    public String acceptInvite(@AuthenticationPrincipal CustomUser user,
                               @PathVariable Long boardId,
                               @RequestParam(required = false) String redirectUrl) {
        if (!boardMemberService.isInvited(boardId, user.getId())) {
            return "redirect:/member/mypage";
        }
        boardMemberService.updateRole(boardId, user.getId(), BoardRole.USER);
        return redirectUrl != null ? "redirect:" + redirectUrl : "redirect:/member/mypage";
    }

    /**
     * 초대 거절 — board_members 레코드 삭제
     */
    @PostMapping("/boards/{boardId}/reject")
    public String rejectInvite(@AuthenticationPrincipal CustomUser user,
                               @PathVariable Long boardId,
                               @RequestParam(required = false) String redirectUrl) {
        boardMemberService.delete(boardId, user.getId());
        return redirectUrl != null ? "redirect:" + redirectUrl : "redirect:/member/mypage";
    }
}
package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.MyPageController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyPageController.class)
class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private BoardMemberService boardMemberService;

    @MockBean
    private PostService postService;

    @MockBean
    private UserDetailsService userDetailsService;

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 1L;

    private CustomUser mockUser() {
        return new CustomUser(USER_ID, "test_user", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority(MemberRole.ROLE_USER.name())));
    }

    // ── 1. GET /member/mypage ──

    @Test
    @DisplayName("GET /member/mypage - 마이페이지 뷰 반환")
    void myPage() throws Exception {
        given(boardMemberService.getInvitedBoards(USER_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/member/mypage").with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "test_user"))
                .andExpect(model().attributeExists("invitedBoards"))
                .andExpect(view().name("mypage"));
    }

    // ── 2. POST /member/mypage/password ──

    @Test
    @DisplayName("POST /member/mypage/password - 비밀번호 변경 성공 → 로그인 페이지 리다이렉트")
    void changePassword_success() throws Exception {
        given(memberService.changePassword(USER_ID, "oldpw", "newpw1234")).willReturn(true);

        mockMvc.perform(post("/member/mypage/password")
                        .param("currentPassword", "oldpw")
                        .param("newPassword", "newpw1234")
                        .param("newPasswordConfirm", "newpw1234")
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login?passwordChanged=true"));
    }

    @Test
    @DisplayName("POST /member/mypage/password - 새 비밀번호 불일치 → mypage 반환")
    void changePassword_mismatch() throws Exception {
        given(boardMemberService.getInvitedBoards(USER_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(post("/member/mypage/password")
                        .param("currentPassword", "oldpw")
                        .param("newPassword", "newpw1234")
                        .param("newPasswordConfirm", "different!")
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage"));
    }

    @Test
    @DisplayName("POST /member/mypage/password - 현재 비밀번호 불일치 → mypage 반환")
    void changePassword_wrongCurrent() throws Exception {
        given(memberService.changePassword(USER_ID, "wrongpw", "newpw1234")).willReturn(false);
        given(boardMemberService.getInvitedBoards(USER_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(post("/member/mypage/password")
                        .param("currentPassword", "wrongpw")
                        .param("newPassword", "newpw1234")
                        .param("newPasswordConfirm", "newpw1234")
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage"));
    }

    // ── 3. POST /member/mypage/boards/{boardId}/accept ──

    @Test
    @DisplayName("POST /boards/{boardId}/accept - from 없음 → 마이페이지 리다이렉트")
    void acceptInvite_toMypage() throws Exception {
        mockMvc.perform(post("/member/mypage/boards/{boardId}/accept", BOARD_ID)
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/mypage"));

        verify(boardMemberService).updateRole(BOARD_ID, USER_ID, BoardRole.USER);
    }

    @Test
    @DisplayName("POST /boards/{boardId}/accept?from=board - 해당 보드로 리다이렉트")
    void acceptInvite_fromBoard() throws Exception {
        mockMvc.perform(post("/member/mypage/boards/{boardId}/accept", BOARD_ID)
                        .param("from", "board")
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID));

        verify(boardMemberService).updateRole(BOARD_ID, USER_ID, BoardRole.USER);
    }

    // ── 4. POST /member/mypage/boards/{boardId}/reject ──

    @Test
    @DisplayName("POST /boards/{boardId}/reject - from 없음 → 마이페이지 리다이렉트")
    void rejectInvite_toMypage() throws Exception {
        mockMvc.perform(post("/member/mypage/boards/{boardId}/reject", BOARD_ID)
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/mypage"));

        verify(boardMemberService).delete(BOARD_ID, USER_ID);
    }

    @Test
    @DisplayName("POST /boards/{boardId}/reject?from=board - 보드 목록으로 리다이렉트")
    void rejectInvite_fromBoard() throws Exception {
        mockMvc.perform(post("/member/mypage/boards/{boardId}/reject", BOARD_ID)
                        .param("from", "board")
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board"));

        verify(boardMemberService).delete(BOARD_ID, USER_ID);
    }
}
package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.MyPageController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostMemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired private MockMvc mockMvc;

    @MockBean private MemberService memberService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostMemberService postMemberService;
    @MockBean private BoardService boardService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long USER_ID = 1L;
    private static final Long BOARD_ID = 5L;

    private CustomUser mockUser() {
        return new CustomUser(USER_ID, "tester", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @BeforeEach
    void setUp() {
        given(boardMemberService.getInvitedBoards(USER_ID)).willReturn(Collections.emptyList());
        given(postMemberService.getAssignedPosts(USER_ID)).willReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("GET /members/mypage - 마이페이지")
    void myPage() throws Exception {
        mockMvc.perform(get("/members/mypage").with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("username", "invitedBoards", "assignedPosts"))
                .andExpect(view().name("mypage"));
    }

    @Test
    @DisplayName("POST /members/mypage/password - 비밀번호 변경 성공")
    void changePassword_success() throws Exception {
        given(memberService.changePassword(USER_ID, "oldpw", "newpw1234")).willReturn(true);

        mockMvc.perform(post("/members/mypage/password")
                        .param("currentPassword", "oldpw")
                        .param("newPassword", "newpw1234")
                        .param("newPasswordConfirm", "newpw1234")
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/login?passwordChanged=true"));
    }

    @Test
    @DisplayName("POST /members/mypage/password - 현재 비밀번호 불일치")
    void changePassword_wrongCurrent() throws Exception {
        given(memberService.changePassword(USER_ID, "wrong", "newpw1234")).willReturn(false);

        mockMvc.perform(post("/members/mypage/password")
                        .param("currentPassword", "wrong")
                        .param("newPassword", "newpw1234")
                        .param("newPasswordConfirm", "newpw1234")
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage"));
    }

    @Test
    @DisplayName("POST /members/mypage/boards/{boardId}/accept - 초대 수락")
    void acceptInvite() throws Exception {
        given(boardMemberService.isInvited(BOARD_ID, USER_ID)).willReturn(true);

        mockMvc.perform(post("/members/mypage/boards/{boardId}/accept", BOARD_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/mypage"));

        verify(boardMemberService).updateRole(BOARD_ID, USER_ID, BoardRole.USER);
    }

    @Test
    @DisplayName("POST /members/mypage/boards/{boardId}/accept - 초대 안 된 경우 리다이렉트")
    void acceptInvite_notInvited() throws Exception {
        given(boardMemberService.isInvited(BOARD_ID, USER_ID)).willReturn(false);

        mockMvc.perform(post("/members/mypage/boards/{boardId}/accept", BOARD_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/mypage"));
    }

    @Test
    @DisplayName("POST /members/mypage/boards/{boardId}/reject - 초대 거절")
    void rejectInvite() throws Exception {
        mockMvc.perform(post("/members/mypage/boards/{boardId}/reject", BOARD_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/mypage"));

        verify(boardMemberService).rejectInvite(BOARD_ID, USER_ID);
    }
}

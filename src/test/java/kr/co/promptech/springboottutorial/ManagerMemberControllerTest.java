package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.ManagerMemberController;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
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

@WebMvcTest(ManagerMemberController.class)
class ManagerMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardMemberService boardMemberService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PostService postService;

    @MockBean
    private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long TARGET_MEMBER_ID = 2L;

    private CustomUser mockUser() {
        return new CustomUser(USER_ID, "test_fe", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority(MemberRole.ROLE_USER.name())));
    }

    @BeforeEach
    void setUpInterceptors() {
        Member member = Member.builder().id(USER_ID).username("test_fe").role(MemberRole.ROLE_USER).build();
        given(memberService.getMemberByUsername("test_fe")).willReturn(new MemberResponseDto(member));
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
        given(boardMemberService.isManager(BOARD_ID, USER_ID)).willReturn(true);
    }

    // ── 1. GET /board/{boardId}/manager/members ──

    @Test
    @DisplayName("GET /board/{boardId}/manager/members - 멤버 목록 페이지 반환")
    void membersPage() throws Exception {
        given(boardMemberService.getMembersByBoardId(BOARD_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board/{boardId}/manager/members", BOARD_ID)
                        .with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("boardId", BOARD_ID))
                .andExpect(view().name("manager/members"));
    }

    // ── 2. POST /approve/{memberId} ──

    @Test
    @DisplayName("POST /approve/{memberId} - REQUESTED 멤버를 USER로 승인")
    void approveMember() throws Exception {
        mockMvc.perform(post("/board/{boardId}/manager/members/approve/{memberId}", BOARD_ID, TARGET_MEMBER_ID)
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).updateRole(BOARD_ID, TARGET_MEMBER_ID, BoardRole.USER);
    }

    // ── 3. POST /promote/{memberId} ──

    @Test
    @DisplayName("POST /promote/{memberId} - USER를 MANAGER로 승격")
    void promoteMember() throws Exception {
        mockMvc.perform(post("/board/{boardId}/manager/members/promote/{memberId}", BOARD_ID, TARGET_MEMBER_ID)
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).updateRole(BOARD_ID, TARGET_MEMBER_ID, BoardRole.MANAGER);
    }

    // ── 4. POST /demote/{memberId} ──

    @Test
    @DisplayName("POST /demote/{memberId} - MANAGER를 USER로 강등")
    void demoteMember() throws Exception {
        mockMvc.perform(post("/board/{boardId}/manager/members/demote/{memberId}", BOARD_ID, TARGET_MEMBER_ID)
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).updateRole(BOARD_ID, TARGET_MEMBER_ID, BoardRole.USER);
    }

    // ── 5. POST /remove/{memberId} ──

    @Test
    @DisplayName("POST /remove/{memberId} - 멤버를 보드에서 제거")
    void removeMember() throws Exception {
        mockMvc.perform(post("/board/{boardId}/manager/members/remove/{memberId}", BOARD_ID, TARGET_MEMBER_ID)
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).delete(BOARD_ID, TARGET_MEMBER_ID);
    }
}

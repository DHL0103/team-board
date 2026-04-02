package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.rest.RestAdminController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.MemberBoardDto;
import kr.co.promptech.springboottutorial.model.dto.MemberPageDto;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import kr.co.promptech.springboottutorial.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import kr.co.promptech.springboottutorial.model.dto.MemberSearchParam;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestAdminController.class)
@Import(SecurityConfig.class)
class RestAdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BoardMemberService boardMemberService;
    @MockBean private MemberService memberService;
    @MockBean private BoardService boardService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private CustomUser adminUser() {
        return new CustomUser(1L, "admin", "pw", MemberRole.ROLE_ADMIN,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private CustomUser normalUser() {
        return new CustomUser(2L, "user", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/admin/members - 멤버 목록 조회")
    void getMembers() throws Exception {
        given(memberService.getMemberPage(any(MemberSearchParam.class)))
                .willReturn(new MemberPageDto(Collections.emptyList(), false, 0L));

        mockMvc.perform(get("/api/admin/members")
                        .with(user(adminUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(0));
    }

    @Test
    @DisplayName("GET /api/admin/members - 일반 유저 403")
    void getMembers_forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/members")
                        .with(user(normalUser())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/members/{memberId}/boards - 멤버 소속 보드 조회")
    void getMemberBoards() throws Exception {
        given(boardMemberService.getBoardsByMemberId(2L))
                .willReturn(List.of(new MemberBoardDto(1L, "보드", "p1", "MANAGER")));

        mockMvc.perform(get("/api/admin/members/2/boards")
                        .with(user(adminUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].boardName").value("보드"));
    }

    @Test
    @DisplayName("POST /api/admin/boards/{boardId}/status - 보드 상태 변경")
    void updateBoardStatus() throws Exception {
        mockMvc.perform(post("/api/admin/boards/1/status")
                        .param("status", "INACTIVE")
                        .with(user(adminUser())).with(csrf()))
                .andExpect(status().isOk());

        verify(boardService).updateStatus(1L, BoardStatus.INACTIVE);
    }

    @Test
    @DisplayName("POST /api/admin/members/{memberId}/role - 역할 변경")
    void updateMemberRole() throws Exception {
        mockMvc.perform(post("/api/admin/members/2/role")
                        .param("role", "ROLE_USER")
                        .with(user(adminUser())).with(csrf()))
                .andExpect(status().isOk());

        verify(memberService).updateRole(2L, MemberRole.ROLE_USER);
    }

    @Test
    @DisplayName("POST /api/admin/members/{memberId}/role - ROLE_ADMIN 변경 시도 400")
    void updateMemberRole_adminForbidden() throws Exception {
        doThrow(new IllegalArgumentException("ROLE_ADMIN 변경 불가"))
                .when(memberService).updateRole(eq(2L), eq(MemberRole.ROLE_ADMIN));

        mockMvc.perform(post("/api/admin/members/2/role")
                        .param("role", "ROLE_ADMIN")
                        .with(user(adminUser())).with(csrf()))
                .andExpect(status().isBadRequest());
    }
}

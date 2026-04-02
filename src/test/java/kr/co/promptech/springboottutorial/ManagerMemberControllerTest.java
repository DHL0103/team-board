package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.ManagerMemberController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerMemberController.class)
class ManagerMemberControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BoardMemberService boardMemberService;
    @MockBean private BoardService boardService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long MEMBER_ID = 2L;
    private static final Long USER_ID = 1L;

    private CustomUser mockUser() {
        return new CustomUser(USER_ID, "manager", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @BeforeEach
    void setUp() {
        given(boardService.getBoardById(BOARD_ID))
                .willReturn(Board.builder().id(BOARD_ID).status(BoardStatus.ACTIVE).build());
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
        given(boardMemberService.isManager(BOARD_ID, USER_ID)).willReturn(true);
    }

    @Test
    @DisplayName("GET /manager/members - 멤버 관리 페이지")
    void membersPage() throws Exception {
        given(boardService.getBoardDtoById(BOARD_ID))
                .willReturn(new BoardResponseDto(BOARD_ID, "보드", "설명", "p1", "ACTIVE", 3L));
        given(boardMemberService.getMembersByBoardId(BOARD_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/boards/{boardId}/manager/members", BOARD_ID).with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("board", "memberList"))
                .andExpect(view().name("manager/members"));
    }

    @Test
    @DisplayName("POST /manager/members/invite/{memberId} - 초대")
    void inviteMember() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/members/invite/{memberId}", BOARD_ID, MEMBER_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).save(BOARD_ID, MEMBER_ID, BoardRole.INVITED);
    }

    @Test
    @DisplayName("POST /manager/members/approve/{memberId} - 가입 승인")
    void approveMember() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/members/approve/{memberId}", BOARD_ID, MEMBER_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).updateRole(BOARD_ID, MEMBER_ID, BoardRole.USER);
    }

    @Test
    @DisplayName("POST /manager/members/promote/{memberId} - 매니저 승격")
    void promoteMember() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/members/promote/{memberId}", BOARD_ID, MEMBER_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).updateRole(BOARD_ID, MEMBER_ID, BoardRole.MANAGER);
    }

    @Test
    @DisplayName("POST /manager/members/demote/{memberId} - 매니저 강등")
    void demoteMember() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/members/demote/{memberId}", BOARD_ID, MEMBER_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).demoteMember(BOARD_ID, MEMBER_ID);
    }

    @Test
    @DisplayName("POST /manager/members/demote/{memberId} - 마지막 매니저 에러")
    void demoteMember_lastManager() throws Exception {
        doThrow(new IllegalStateException("last_manager"))
                .when(boardMemberService).demoteMember(BOARD_ID, MEMBER_ID);

        mockMvc.perform(post("/boards/{boardId}/manager/members/demote/{memberId}", BOARD_ID, MEMBER_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/members?error=last_manager"));
    }

    @Test
    @DisplayName("POST /manager/members/remove/{memberId} - 멤버 제거")
    void removeMember() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/members/remove/{memberId}", BOARD_ID, MEMBER_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/members"));

        verify(boardMemberService).removeMember(BOARD_ID, MEMBER_ID);
    }
}

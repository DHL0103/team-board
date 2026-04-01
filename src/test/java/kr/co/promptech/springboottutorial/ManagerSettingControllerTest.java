package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.ManagerSettingController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerSettingController.class)
class ManagerSettingControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
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
    @DisplayName("GET /manager/settings - 설정 페이지")
    void settingsPage() throws Exception {
        given(boardService.getBoardDtoById(BOARD_ID))
                .willReturn(new BoardResponseDto(BOARD_ID, "보드", "설명", "p1", "ACTIVE", 3L));

        mockMvc.perform(get("/board/{boardId}/manager/settings", BOARD_ID).with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("board"))
                .andExpect(view().name("manager/settings"));
    }

    @Test
    @DisplayName("POST /manager/settings - 보드 설정 수정")
    void updateSettings() throws Exception {
        mockMvc.perform(post("/board/{boardId}/manager/settings", BOARD_ID)
                        .param("name", "수정 보드").param("color", "p2").param("status", "ACTIVE")
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/manager/settings"));

        verify(boardService).updateBoard(eq(BOARD_ID), any());
    }
}

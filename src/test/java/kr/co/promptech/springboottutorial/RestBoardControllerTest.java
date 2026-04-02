package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.rest.RestBoardController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.BoardPageDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestBoardController.class)
class RestBoardControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private CustomUser mockUser() {
        return new CustomUser(1L, "user", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/boards - 보드 목록 조회")
    void getBoards() throws Exception {
        given(boardService.getBoardDtosByStatus(eq("ACTIVE"), eq(""), any()))
                .willReturn(List.of(new BoardResponseDto(1L, "보드", "설명", "p1", "ACTIVE", 3L)));

        mockMvc.perform(get("/api/boards")
                        .param("status", "ACTIVE")
                        .with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("보드"));
    }

    @Test
    @DisplayName("GET /api/boards/search - 보드 검색")
    void searchBoards() throws Exception {
        given(boardService.getBoardPageForSearch(any(), eq(1L)))
                .willReturn(new BoardPageDto(Collections.emptyList(), false, 0L));

        mockMvc.perform(get("/api/boards/search")
                        .param("boardName", "test")
                        .param("boardStatus", "")
                        .param("sort", "recent")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(0));
    }
}

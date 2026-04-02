package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.rest.RestPostController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.PostPageDto;
import kr.co.promptech.springboottutorial.model.dto.PostSearchParam;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestPostController.class)
class RestPostControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostService postService;
    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long USER_ID = 1L;

    private CustomUser mockUser() {
        return new CustomUser(USER_ID, "user", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @BeforeEach
    void setUp() {
        given(boardService.getBoardById(BOARD_ID))
                .willReturn(Board.builder().id(BOARD_ID).status(BoardStatus.ACTIVE).build());
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
    }

    @Test
    @DisplayName("GET /api/boards/{boardId}/posts - 게시글 목록 조회")
    void getPosts() throws Exception {
        given(postService.getPostPage(eq(BOARD_ID), any(PostSearchParam.class), eq(USER_ID)))
                .willReturn(new PostPageDto(Collections.emptyList(), false, 0L));

        mockMvc.perform(get("/api/boards/{boardId}/posts", BOARD_ID)
                        .param("postStatus", "APPROVED")
                        .param("postTitle", "")
                        .param("sort", "newest")
                        .param("page", "0")
                        .param("size", "8")
                        .with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(0));
    }
}

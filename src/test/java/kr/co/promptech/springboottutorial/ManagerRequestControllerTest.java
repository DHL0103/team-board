package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.ManagerRequestController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
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
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerRequestController.class)
class ManagerRequestControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostService postService;
    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long POST_ID = 10L;
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
    @DisplayName("GET /manager/requests - 승인 요청 목록")
    void requestsPage() throws Exception {
        given(boardService.getBoardDtoById(BOARD_ID))
                .willReturn(new BoardResponseDto(BOARD_ID, "테스트", "설명", "p1", "ACTIVE", 3L));
        given(postService.getRequestedPostDtosByBoardId(BOARD_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/boards/{boardId}/manager/requests", BOARD_ID).with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("board", "requestList"))
                .andExpect(view().name("manager/requests"));
    }

    @Test
    @DisplayName("POST /manager/requests/approve/{postId} - 승인 후 목록으로")
    void approve() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/requests/approve/{postId}", BOARD_ID, POST_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/requests"));

        verify(postService).updateStatus(POST_ID, PostStatus.APPROVED);
    }

    @Test
    @DisplayName("POST /manager/requests/approve/{postId} - source=detail이면 상세로")
    void approve_fromDetail() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/requests/approve/{postId}", BOARD_ID, POST_ID)
                        .param("source", "detail")
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/posts/" + POST_ID));
    }

    @Test
    @DisplayName("POST /manager/requests/reject/{postId} - 반려")
    void reject() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/manager/requests/reject/{postId}", BOARD_ID, POST_ID)
                        .param("reason", "사유")
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/manager/requests"));

        verify(postService).rejectPost(POST_ID, "사유", USER_ID);
    }
}

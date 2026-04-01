package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.PostController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.model.dto.PostDetailDto;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostService postService;
    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long POST_ID = 10L;
    private static final Long USER_ID = 1L;

    private CustomUser mockUser(MemberRole role) {
        return new CustomUser(USER_ID, "tester", "pw", role, List.of(new SimpleGrantedAuthority(role.name())));
    }

    private Post mockPost(String status) {
        return Post.builder()
                .id(POST_ID).boardId(BOARD_ID).memberId(USER_ID)
                .title("테스트").content("내용").status(status)
                .createdAt(LocalDateTime.now()).build();
    }

    @BeforeEach
    void setUp() {
        given(boardService.getBoardById(BOARD_ID))
                .willReturn(Board.builder().id(BOARD_ID).name("테스트보드").status(BoardStatus.ACTIVE).build());
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
        given(postService.getPostById(POST_ID)).willReturn(mockPost("PROGRESS"));
        given(postService.isAssignee(POST_ID, USER_ID)).willReturn(true);
    }

    // ── GET /board/{boardId}/post/{postId} ──

    @Test
    @DisplayName("GET /post/{postId} - 포스트 상세 페이지")
    void getDetail() throws Exception {
        given(postService.getPostDetail(eq(POST_ID), any(CustomUser.class)))
                .willReturn(PostDetailDto.builder().id(POST_ID).boardId(BOARD_ID)
                        .title("테스트").status("PROGRESS").canModify(true)
                        .boardUserList(Collections.emptyList()).postAssignees(Collections.emptyList())
                        .commentList(Collections.emptyList()).build());

        mockMvc.perform(get("/board/{boardId}/post/{postId}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("post"))
                .andExpect(view().name("post/detail"));
    }

    // ── POST /board/{boardId}/post/create ──

    @Test
    @DisplayName("POST /post/create - 게시글 생성 후 리다이렉트")
    void createPost() throws Exception {
        mockMvc.perform(post("/board/{boardId}/post/create", BOARD_ID)
                        .param("title", "새 포스트").param("content", "내용").param("boardId", BOARD_ID.toString())
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID));

        verify(postService).createPost(any(), eq(USER_ID), any());
    }

    // ── POST /board/{boardId}/post/delete/{id} ──

    @Test
    @DisplayName("POST /post/delete/{id} - 삭제 후 리다이렉트")
    void deletePost() throws Exception {
        mockMvc.perform(post("/board/{boardId}/post/delete/{id}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID));

        verify(postService).deletePost(POST_ID);
    }

    @Test
    @DisplayName("POST /post/delete/{id} - REQUESTED 상태 담당자는 403")
    void deletePost_requested_forbidden() throws Exception {
        given(postService.getPostById(POST_ID)).willReturn(mockPost("REQUESTED"));

        mockMvc.perform(post("/board/{boardId}/post/delete/{id}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ── POST /board/{boardId}/post/update/{id} ──

    @Test
    @DisplayName("POST /post/update/{id} - 수정 후 상세로 리다이렉트")
    void updatePost() throws Exception {
        mockMvc.perform(post("/board/{boardId}/post/update/{id}", BOARD_ID, POST_ID)
                        .param("title", "수정").param("content", "수정내용")
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/post/" + POST_ID));

        verify(postService).updatePost(eq(POST_ID), any(), any(), any());
    }

    // ── POST /board/{boardId}/post/request/{id} ──

    @Test
    @DisplayName("POST /post/request/{id} - 승인 요청")
    void requestPost() throws Exception {
        mockMvc.perform(post("/board/{boardId}/post/request/{id}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/post/" + POST_ID));

        verify(postService).updateStatus(POST_ID, PostStatus.REQUESTED);
    }
}

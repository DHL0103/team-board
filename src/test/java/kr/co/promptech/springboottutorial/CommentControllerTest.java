package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.CommentController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.CommentService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private CommentService commentService;
    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long POST_ID = 10L;
    private static final Long COMMENT_ID = 100L;
    private static final Long USER_ID = 1L;

    private CustomUser mockUser() {
        return new CustomUser(USER_ID, "tester", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @BeforeEach
    void setUp() {
        given(boardService.getBoardById(BOARD_ID))
                .willReturn(Board.builder().id(BOARD_ID).status(BoardStatus.ACTIVE).build());
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
        given(postService.getPostById(POST_ID))
                .willReturn(Post.builder().id(POST_ID).boardId(BOARD_ID).memberId(USER_ID)
                        .title("t").content("c").status("PROGRESS").createdAt(LocalDateTime.now()).build());
    }

    @Test
    @DisplayName("POST /comments - 댓글 작성 후 리다이렉트")
    void addComment() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/posts/{postId}/comments", BOARD_ID, POST_ID)
                        .param("postId", POST_ID.toString()).param("content", "댓글 내용")
                        .param("depth", "0")
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/posts/" + POST_ID + "#comment-section"));

        verify(commentService).save(eq(USER_ID), any());
    }

    @Test
    @DisplayName("POST /comments/{commentId}/edit - 댓글 수정 후 리다이렉트")
    void editComment() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/posts/{postId}/comments/{commentId}/edit", BOARD_ID, POST_ID, COMMENT_ID)
                        .param("content", "수정 댓글")
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/posts/" + POST_ID + "#comment-section"));

        verify(commentService).update(COMMENT_ID, USER_ID, "수정 댓글");
    }

    @Test
    @DisplayName("POST /comments/{commentId}/delete - 댓글 삭제 후 리다이렉트")
    void deleteComment() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/posts/{postId}/comments/{commentId}/delete", BOARD_ID, POST_ID, COMMENT_ID)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/posts/" + POST_ID + "#comment-section"));

        verify(commentService).delete(COMMENT_ID, USER_ID, MemberRole.ROLE_USER, BOARD_ID);
    }
}

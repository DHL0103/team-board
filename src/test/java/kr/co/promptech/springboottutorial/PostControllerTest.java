package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.PostController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostFileService;
import kr.co.promptech.springboottutorial.service.PostRejectionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private PostService postService;
    @MockBean private PostFileService postFileService;
    @MockBean private BoardService boardService;
    @MockBean private PostRejectionService postRejectionService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private MemberService memberService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long POST_ID = 10L;
    private static final Long USER_ID = 1L;

    private CustomUser mockUser(MemberRole role) {
        return new CustomUser(USER_ID, "tester", "pw", role,
                List.of(new SimpleGrantedAuthority(role.name())));
    }

    private Post mockPost(String status) {
        return Post.builder()
                .id(POST_ID).boardId(BOARD_ID).memberId(USER_ID)
                .title("테스트 포스트").content("내용").status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @BeforeEach
    void setUpInterceptors() {
        Member member = Member.builder().id(USER_ID).username("tester").role(MemberRole.ROLE_USER).build();
        given(memberService.getMemberByUsername("tester")).willReturn(new MemberResponseDto(member));
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);

        Post defaultPost = mockPost("PROGRESS");
        given(postService.getPostById(POST_ID)).willReturn(defaultPost);
        given(boardService.getBoardById(BOARD_ID)).willReturn(
                Board.builder().id(BOARD_ID).name("테스트보드").color("p1").build());
        given(postFileService.getFilesByPostId(POST_ID)).willReturn(Collections.emptyList());
        given(postRejectionService.getByPostId(POST_ID)).willReturn(Collections.emptyList());
        given(boardMemberService.getUsersByBoardId(BOARD_ID)).willReturn(Collections.emptyList());
        given(postService.getAssigneesByPostId(POST_ID)).willReturn(Collections.emptyList());
    }

    // ── 1. GET /board/{boardId}/post/{postId} ──

    @Test
    @DisplayName("GET /{postId} - canModify=true (postService.canModify가 true 반환)")
    void getDetail_canModify_true() throws Exception {
        given(postService.canModify(POST_ID, BOARD_ID, USER_ID, MemberRole.ROLE_USER)).willReturn(true);

        mockMvc.perform(get("/board/{boardId}/post/{postId}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("canModify", true))
                .andExpect(view().name("post/detail"));
    }

    @Test
    @DisplayName("GET /{postId} - REQUESTED 상태 담당자는 canModify=false")
    void getDetail_requested_canModify_false() throws Exception {
        given(postService.getPostById(POST_ID)).willReturn(mockPost("REQUESTED"));
        given(postService.canModify(POST_ID, BOARD_ID, USER_ID, MemberRole.ROLE_USER)).willReturn(false);

        mockMvc.perform(get("/board/{boardId}/post/{postId}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("canModify", false))
                .andExpect(view().name("post/detail"));
    }

    @Test
    @DisplayName("GET /{postId} - APPROVED 상태 담당자는 canModify=false")
    void getDetail_approved_canModify_false() throws Exception {
        given(postService.getPostById(POST_ID)).willReturn(mockPost("APPROVED"));
        given(postService.canModify(POST_ID, BOARD_ID, USER_ID, MemberRole.ROLE_USER)).willReturn(false);

        mockMvc.perform(get("/board/{boardId}/post/{postId}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("canModify", false))
                .andExpect(view().name("post/detail"));
    }

    // ── 2. POST /board/{boardId}/post/create ──

    @Test
    @DisplayName("POST /create - 게시글 생성 후 보드 상세로 리다이렉트")
    void createPost() throws Exception {
        given(postService.createPost(any(), any())).willReturn(POST_ID);

        mockMvc.perform(post("/board/{boardId}/post/create", BOARD_ID)
                        .param("title", "새 포스트")
                        .param("content", "내용")
                        .param("boardId", BOARD_ID.toString())
                        .with(user(mockUser(MemberRole.ROLE_USER)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID));

        verify(postService).createPost(any(), any());
    }

    // ── 3. POST /board/{boardId}/post/delete/{id} ──

    @Test
    @DisplayName("POST /delete/{id} - 게시글 삭제 후 보드 상세로 리다이렉트")
    void deletePost() throws Exception {
        Post post = mockPost("PROGRESS");
        given(postService.getPostById(POST_ID)).willReturn(post);
        given(postService.isAssignee(POST_ID, USER_ID)).willReturn(true);

        mockMvc.perform(post("/board/{boardId}/post/delete/{id}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID));

        verify(postService).deletePost(post);
    }

    @Test
    @DisplayName("POST /delete/{id} - REQUESTED 상태에서 담당자는 403")
    void deletePost_requested_forbidden() throws Exception {
        given(postService.getPostById(POST_ID)).willReturn(mockPost("REQUESTED"));
        given(postService.isAssignee(POST_ID, USER_ID)).willReturn(true);

        mockMvc.perform(post("/board/{boardId}/post/delete/{id}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER)))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ── 4. POST /board/{boardId}/post/update/{id} ──

    @Test
    @DisplayName("POST /update/{id} - 게시글 수정 후 상세 페이지로 리다이렉트")
    void updatePost() throws Exception {
        given(postService.isAssignee(POST_ID, USER_ID)).willReturn(true);

        mockMvc.perform(post("/board/{boardId}/post/update/{id}", BOARD_ID, POST_ID)
                        .param("title", "수정 제목")
                        .param("content", "수정 내용")
                        .with(user(mockUser(MemberRole.ROLE_USER)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/post/" + POST_ID));

        verify(postService).updatePost(any(), any());
    }

    @Test
    @DisplayName("POST /update/{id} - REQUESTED 상태에서 담당자는 403")
    void updatePost_requested_forbidden() throws Exception {
        given(postService.getPostById(POST_ID)).willReturn(mockPost("REQUESTED"));
        given(postService.isAssignee(POST_ID, USER_ID)).willReturn(true);

        mockMvc.perform(post("/board/{boardId}/post/update/{id}", BOARD_ID, POST_ID)
                        .param("title", "수정 제목")
                        .param("content", "수정 내용")
                        .with(user(mockUser(MemberRole.ROLE_USER)))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ── 5. POST /board/{boardId}/post/request/{id} ──

    @Test
    @DisplayName("POST /request/{id} - 상태 REQUESTED로 변경 후 상세 페이지로 리다이렉트")
    void requestPost() throws Exception {
        given(postService.isAssignee(POST_ID, USER_ID)).willReturn(true);

        mockMvc.perform(post("/board/{boardId}/post/request/{id}", BOARD_ID, POST_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/post/" + POST_ID));

        verify(postService).updateStatus(POST_ID, PostStatus.REQUESTED);
    }
}
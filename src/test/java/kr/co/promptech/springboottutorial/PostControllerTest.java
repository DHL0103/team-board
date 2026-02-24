package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.BoardController;
import kr.co.promptech.springboottutorial.controller.PostController;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({PostController.class, BoardController.class})
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private UserDetailsService userDetailsService;

    // ── 헬퍼 메서드 ──

    private Member mockMember(Long id) {
        Member member = new Member();
        member.setId(id);
        return member;
    }

    private Post mockPost(Long id, Long memberId) {
        Post post = new Post();
        post.setId(id);
        post.setMemberId(memberId);
        return post;
    }

    // ── 1. GET /board - 전체 게시글 조회 ──

    @Test
    @WithMockUser
    @DisplayName("GET /board - 전체 게시글 조회 테스트")
    void testGetAllBoardView() throws Exception {
        given(postService.getAllPost()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postList"))
                .andExpect(view().name("main_page"));
    }

    // ── 2. GET /post/{id} - 상세 페이지 조회 ──

    @Test
    @WithMockUser(username = "test_fe")
    @DisplayName("GET /post/{id} - 본인 글 상세 페이지 조회 시 isOwner=true")
    void testGetPostDetailPage_owner() throws Exception {
        Long postId = 1L;
        Post mockPost = mockPost(postId, 1L);
        Member mockMember = mockMember(1L);

        given(postService.getPostById(postId)).willReturn(mockPost);
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMember);

        mockMvc.perform(get("/post/" + postId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("isOwner", true))
                .andExpect(view().name("post/detail"));
    }

    @Test
    @WithMockUser(username = "test_fe")
    @DisplayName("GET /post/{id} - 타인 글 상세 페이지 조회 시 isOwner=false")
    void testGetPostDetailPage_nonOwner() throws Exception {
        Long postId = 1L;
        Post mockPost = mockPost(postId, 99L); // 다른 사람 글
        Member mockMember = mockMember(1L);

        given(postService.getPostById(postId)).willReturn(mockPost);
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMember);

        mockMvc.perform(get("/post/" + postId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isOwner", false))
                .andExpect(view().name("post/detail"));
    }

    // ── 3. POST /post/create - 게시글 생성 ──

    @Test
    @WithMockUser(username = "test_fe")
    @DisplayName("POST /post/create - 게시글 생성 테스트")
    void testCreatePost() throws Exception {
        mockMvc.perform(post("/post/create")
                        .param("boardId", "1")
                        .param("title", "제목")
                        .param("content", "내용")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board"));

        verify(postService).createPost(any(PostCreateDto.class), eq("test_fe"));
    }

    // ── 4. POST /post/delete/{id} - 게시글 삭제 ──

    @Test
    @WithMockUser(username = "test_fe")
    @DisplayName("POST /post/delete/{id} - 본인 글 삭제 성공")
    void testDeletePost_owner() throws Exception {
        Long postId = 1L;
        Post mockPost = mockPost(postId, 1L);
        Member mockMember = mockMember(1L);

        given(postService.getPostById(postId)).willReturn(mockPost);
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMember);

        mockMvc.perform(post("/post/delete/" + postId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board"));

        verify(postService).deletePost(mockPost);
    }

    @Test
    @WithMockUser(username = "test_fe")
    @DisplayName("POST /post/delete/{id} - 타인 글 삭제 거부")
    void testDeletePost_nonOwner() throws Exception {
        Long postId = 1L;
        Post mockPost = mockPost(postId, 99L); // 다른 사람 글
        Member mockMember = mockMember(1L);

        given(postService.getPostById(postId)).willReturn(mockPost);
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMember);

        mockMvc.perform(post("/post/delete/" + postId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId + "?error=unauthorized"));
    }

    // ── 5. POST /post/update/{id} - 게시글 수정 ──

    @Test
    @WithMockUser(username = "test_fe")
    @DisplayName("POST /post/update/{id} - 본인 글 수정 성공")
    void testUpdatePost_owner() throws Exception {
        Long postId = 1L;
        Post mockPost = mockPost(postId, 1L);
        Member mockMember = mockMember(1L);

        given(postService.getPostById(postId)).willReturn(mockPost);
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMember);

        mockMvc.perform(post("/post/update/" + postId)
                        .param("title", "수정된 제목")
                        .param("content", "수정된 내용")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId));

        verify(postService).updatePost(eq(mockPost), any(PostCreateDto.class));
    }

    @Test
    @WithMockUser(username = "test_fe")
    @DisplayName("POST /post/update/{id} - 타인 글 수정 거부")
    void testUpdatePost_nonOwner() throws Exception {
        Long postId = 1L;
        Post mockPost = mockPost(postId, 99L); // 다른 사람 글
        Member mockMember = mockMember(1L);

        given(postService.getPostById(postId)).willReturn(mockPost);
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMember);

        mockMvc.perform(post("/post/update/" + postId)
                        .param("title", "수정된 제목")
                        .param("content", "수정된 내용")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId + "?error=unauthorized"));
    }
}
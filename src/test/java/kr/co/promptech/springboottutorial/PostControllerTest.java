package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.BoardController;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.controller.PostController;
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
// ... 임포트 생략

@WebMvcTest({PostController.class, BoardController.class})
@AutoConfigureMockMvc // 검문소 가동!
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    @MockBean
    private MemberService memberService;  // ← 추가 필요

    @MockBean
    private UserDetailsService userDetailsService;  // ← SecurityConfig용으로 추가 필요


    @Test
    @WithMockUser // 조회도 로그인 유저여야 통과됨
    @DisplayName("GET /board - 전체 게시글 조회 테스트")
    void testGetAllBoardView() throws Exception {
        given(postService.getAllPost()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postList"))
                .andExpect(view().name("main_page"));
    }

    @Test
    @WithMockUser // 상세페이지도 로그인 유저여야 통과됨
    @DisplayName("GET /post/{id} - 상세 페이지 조회 테스트")
    void testGetPostDetailPage() throws Exception {
        Long postId = 1L;
        Post mockPost = new Post();
        given(postService.getPostById(postId)).willReturn(mockPost);

        mockMvc.perform(get("/post/" + postId))
                .andExpect(status().isOk())
                .andExpect(view().name("post_detail"));
    }

    @Test
    @WithMockUser(username = "daehee_fe")
    @DisplayName("POST /post/create - 게시글 생성 테스트")
    void testCreatePost() throws Exception {
        mockMvc.perform(post("/post/create")
                        .param("boardId", "1")
                        .param("title", "제목")
                        .param("content", "내용")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board"));

        verify(postService).createPost(any(PostCreateDto.class), eq("daehee_fe"));
    }
}


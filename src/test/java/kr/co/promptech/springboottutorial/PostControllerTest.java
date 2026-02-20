package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.post.Post;
import kr.co.promptech.springboottutorial.post.PostController;
import kr.co.promptech.springboottutorial.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
@WebMvcTest(PostController.class) // Controller만 가볍게 띄워서 테스트
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc; // 가상의 HTTP 요청(GET, POST)을 보내는 역할을 하는 객체
    @MockBean
    private PostService postService; // 스프링 컨테이너 안에 가짜 Bean을 생성하여 등록

    @Test
    @DisplayName("GET /board - 전체 게시글 조회 테스트")
    void testGetAllBoardView() throws Exception {
        // given
        given(postService.getAllPost()).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/board"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postList")) // HTML에서 postList로 쓰고 계셔서 수정했습니다.
                .andExpect(view().name("main_page"));
    }

    @Test
    @DisplayName("GET /post/{id} - 상세 페이지 조회 테스트")
    void testGetPostDetailPage() throws Exception {
        // given
        Long postId = 1L;
        Post mockPost = new Post();
        // Post 엔티티에 Builder나 Setter가 있다면 데이터를 세팅해줍니다.
        // 예: mockPost.setTitle("테스트 제목");

        // postService.getPostById(1L)이 호출되면 위에서 만든 가짜 객체를 리턴하라고 명시
        given(postService.getPostById(postId)).willReturn(mockPost);

        // when & then
        mockMvc.perform(get("/post/" + postId))      // /post/1 경로로 GET 요청
                .andExpect(status().isOk())          // 200 OK 응답 확인
                .andExpect(model().attributeExists("post")) // 모델에 "post" 객체가 존재하는지
                .andExpect(model().attribute("post", mockPost)) // 담긴 객체가 우리가 만든 가짜 객체와 같은지
                .andExpect(view().name("post_detail")); // 리턴하는 HTML 파일명이 정확한지
    }
}

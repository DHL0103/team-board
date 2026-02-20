package kr.co.promptech.springboottutorial.board;

import kr.co.promptech.springboottutorial.post.Post;
import kr.co.promptech.springboottutorial.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("GET /board 요청 시 게시글 리스트와 함께 main_page 뷰를 반환해야 한다")
    void testGetAllBoardView() throws Exception {
        // given
        // 서비스에서 데이터가 없다고 빈 리스트를 반환하게 설정 (PostControllerTest 내용 참고)
        given(postService.getAllPost()).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/board")) // /board 로 GET 요청을 보냄
                .andExpect(status().isOk()) // HTTP 상태 코드가 200(OK)인지 확인
                .andExpect(model().attributeExists("postList")); // Model에 "postList"라는 값이 담겼는지 확인
    }
}

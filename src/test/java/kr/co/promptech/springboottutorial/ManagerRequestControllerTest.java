package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.ManagerRequestController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private PostRejectionService postRejectionService;

    @MockBean
    private BoardMemberService boardMemberService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long POST_ID = 10L;

    private CustomUser mockUser() {
        return new CustomUser(USER_ID, "test_fe", "pw", "ROLE_USER",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @BeforeEach
    void setUpInterceptors() {
        Member member = Member.builder().id(USER_ID).username("test_fe").role("ROLE_USER").build();
        given(memberService.getMemberByUsername("test_fe")).willReturn(new MemberResponseDto(member));
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
        given(boardMemberService.isManager(BOARD_ID, USER_ID)).willReturn(true);
    }

    // ── 1. GET /board/{boardId}/manager/requests ──

    @Test
    @DisplayName("GET /board/{boardId}/manager/requests - 승인 요청 목록 페이지 반환")
    void requestsPage() throws Exception {
        given(postService.getRequestedPostDtosByBoardId(BOARD_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board/{boardId}/manager/requests", BOARD_ID)
                        .with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("boardId", BOARD_ID))
                .andExpect(view().name("manager/requests"));
    }

    // ── 2. POST /approve/{postId} ──

    @Test
    @DisplayName("POST /approve/{postId} - 포스트 상태 APPROVED로 변경")
    void approve() throws Exception {
        mockMvc.perform(post("/board/{boardId}/manager/requests/approve/{postId}", BOARD_ID, POST_ID)
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/manager/requests"));

        verify(postService).updateStatus(POST_ID, PostStatus.APPROVED);
    }

    // ── 3. POST /reject/{postId} ──

    @Test
    @DisplayName("POST /reject/{postId} - 포스트 REJECTED 처리 및 반려 사유 저장")
    void reject() throws Exception {
        mockMvc.perform(post("/board/{boardId}/manager/requests/reject/{postId}", BOARD_ID, POST_ID)
                        .param("reason", "내용 보완 필요")
                        .with(user(mockUser()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/manager/requests"));

        verify(postService).updateStatus(POST_ID, PostStatus.REJECTED);
        verify(postRejectionService).save(POST_ID, "내용 보완 필요", USER_ID);
    }
}

package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.BoardController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.model.dto.MemberResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @MockBean
    private PostService postService;

    @MockBean
    private BoardMemberService boardMemberService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long USER_ID = 1L;

    private CustomUser mockUser(String role) {
        return new CustomUser(USER_ID, "test_fe", "pw", role, List.of(new SimpleGrantedAuthority(role)));
    }

    private MemberResponseDto mockMemberDto(String role) {
        Member member = Member.builder().id(USER_ID).username("test_fe").role(role).build();
        return new MemberResponseDto(member);
    }

    private BoardResponseDto mockBoardDto() {
        return new BoardResponseDto(BOARD_ID, "테스트보드", "설명", "p1", "ACTIVE", 0L);
    }

    @BeforeEach
    void setUpInterceptors() {
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMemberDto("ROLE_USER"));
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
    }

    // ── 1. GET /board ──

    @Test
    @DisplayName("GET /board - USER는 소속 보드 목록 반환")
    void getAllBoard_user() throws Exception {
        given(boardService.getBoardDtosByMemberId(USER_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board").with(user(mockUser("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("boardList"))
                .andExpect(view().name("main_page"));

        verify(boardService).getBoardDtosByMemberId(USER_ID);
        verify(boardService, never()).getAllBoardDtos();
    }

    @Test
    @DisplayName("GET /board - ADMIN은 전체 보드 목록 반환")
    void getAllBoard_admin() throws Exception {
        given(boardService.getAllBoardDtos()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board").with(user(mockUser("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("boardList"))
                .andExpect(view().name("main_page"));

        verify(boardService).getAllBoardDtos();
        verify(boardService, never()).getBoardDtosByMemberId(any());
    }

    // ── 2. GET /board/{boardId} ──

    @Test
    @DisplayName("GET /board/{boardId} - USER 멤버 → isManager=false")
    void boardDetail_user() throws Exception {
        given(boardService.getBoardDtoById(BOARD_ID)).willReturn(mockBoardDto());
        given(postService.getPostDtosByBoardId(BOARD_ID)).willReturn(Collections.emptyList());
        given(boardMemberService.isManager(BOARD_ID, USER_ID)).willReturn(false);

        mockMvc.perform(get("/board/{boardId}", BOARD_ID).with(user(mockUser("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isManager", false))
                .andExpect(view().name("board/detail"));
    }

    @Test
    @DisplayName("GET /board/{boardId} - ADMIN → isManager=true")
    void boardDetail_admin() throws Exception {
        given(memberService.getMemberByUsername("test_fe")).willReturn(mockMemberDto("ROLE_ADMIN"));
        given(boardService.getBoardDtoById(BOARD_ID)).willReturn(mockBoardDto());
        given(postService.getPostDtosByBoardId(BOARD_ID)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board/{boardId}", BOARD_ID).with(user(mockUser("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isManager", true))
                .andExpect(view().name("board/detail"));
    }

    // ── 3. GET /board/{boardId}/request (BoardAuthInterceptor 제외 경로) ──

    @Test
    @DisplayName("GET /board/{boardId}/request - 비멤버 → request 뷰 반환")
    void boardRequest_nonMember() throws Exception {
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(false);
        given(boardMemberService.isRequested(BOARD_ID, USER_ID)).willReturn(false);
        given(boardService.getBoardDtoById(BOARD_ID)).willReturn(mockBoardDto());

        mockMvc.perform(get("/board/{boardId}/request", BOARD_ID).with(user(mockUser("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isRequested", false))
                .andExpect(view().name("board/request"));
    }

    @Test
    @DisplayName("GET /board/{boardId}/request - 이미 멤버면 보드 상세로 리다이렉트")
    void boardRequest_alreadyMember() throws Exception {
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);

        mockMvc.perform(get("/board/{boardId}/request", BOARD_ID).with(user(mockUser("ROLE_USER"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID));
    }

    // ── 4. POST /board/{boardId}/request ──

    @Test
    @DisplayName("POST /board/{boardId}/request - REQUESTED 저장 후 리다이렉트")
    void boardRequestPost() throws Exception {
        mockMvc.perform(post("/board/{boardId}/request", BOARD_ID)
                        .with(user(mockUser("ROLE_USER")))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/" + BOARD_ID + "/request"));

        verify(boardMemberService).save(BOARD_ID, USER_ID, "REQUESTED");
    }

    // ── 5. GET /board/{boardId}/post_list ──

    @Test
    @DisplayName("GET /board/{boardId}/post_list - 상태별 포스트 목록 반환")
    void postList() throws Exception {
        given(boardService.getBoardDtoById(BOARD_ID)).willReturn(mockBoardDto());
        given(postService.getPostDtosByBoardIdAndStatus(BOARD_ID, "PROGRESS")).willReturn(Collections.emptyList());

        mockMvc.perform(get("/board/{boardId}/post_list", BOARD_ID)
                        .param("status", "PROGRESS")
                        .with(user(mockUser("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("status", "PROGRESS"))
                .andExpect(view().name("board/post_list"));
    }

    // ── 6. POST /board/create (BoardAuthInterceptor 제외 경로) ──

    @Test
    @DisplayName("POST /board/create - 보드 생성 후 생성자 MANAGER 등록 및 리다이렉트")
    void createBoard() throws Exception {
        given(boardService.createBoard(any(BoardCreateDto.class))).willReturn(BOARD_ID);

        mockMvc.perform(post("/board/create")
                        .param("name", "새 보드")
                        .param("color", "p1")
                        .param("description", "설명")
                        .with(user(mockUser("ROLE_USER")))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board"));

        verify(boardMemberService).save(BOARD_ID, USER_ID, "MANAGER");
    }
}

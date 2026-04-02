package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.BoardController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.dto.BoardDetailDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.PostMemberService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostMemberService postMemberService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private static final Long BOARD_ID = 1L;
    private static final Long USER_ID = 1L;

    private CustomUser mockUser(MemberRole role) {
        return new CustomUser(USER_ID, "tester", "pw", role, List.of(new SimpleGrantedAuthority(role.name())));
    }

    private BoardResponseDto mockBoardDto() {
        return new BoardResponseDto(BOARD_ID, "테스트보드", "설명", "p1", "ACTIVE", 3L);
    }

    @BeforeEach
    void setUp() {
        given(boardService.getBoardById(BOARD_ID))
                .willReturn(Board.builder().id(BOARD_ID).name("테스트보드").status(BoardStatus.ACTIVE).build());
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(true);
        given(postMemberService.getMyPostIds(BOARD_ID, USER_ID)).willReturn(Set.of());
    }

    // ── GET /boards ──

    @Test
    @DisplayName("GET /boards - 메인 페이지 반환")
    void getMainPage() throws Exception {
        mockMvc.perform(get("/boards").with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(view().name("main_page"));
    }

    // ── GET /boards/{boardId} ──

    @Test
    @DisplayName("GET /boards/{boardId} - 보드 상세 페이지 반환")
    void boardDetail() throws Exception {
        given(boardService.getBoardDetail(eq(BOARD_ID), any(CustomUser.class)))
                .willReturn(BoardDetailDto.builder()
                        .id(BOARD_ID).name("테스트보드").description("설명").color("p1")
                        .status("ACTIVE").memberCount(3).isManager(true).isMember(true)
                        .boardUserList(Collections.emptyList()).build());

        mockMvc.perform(get("/boards/{boardId}", BOARD_ID).with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("board", "myPostIds"))
                .andExpect(view().name("board/detail"));
    }

    // ── GET /boards/{boardId}/request ──

    @Test
    @DisplayName("GET /boards/{boardId}/request - 비멤버는 request 뷰 반환")
    void boardRequest_nonMember() throws Exception {
        given(boardMemberService.isMember(BOARD_ID, USER_ID)).willReturn(false);
        given(boardMemberService.isRequested(BOARD_ID, USER_ID)).willReturn(false);
        given(boardMemberService.isInvited(BOARD_ID, USER_ID)).willReturn(false);
        given(boardService.getBoardDtoById(BOARD_ID)).willReturn(mockBoardDto());

        mockMvc.perform(get("/boards/{boardId}/request", BOARD_ID).with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isRequested", false))
                .andExpect(model().attribute("isInvited", false))
                .andExpect(view().name("board/request"));
    }

    @Test
    @DisplayName("GET /boards/{boardId}/request - 이미 멤버면 리다이렉트")
    void boardRequest_alreadyMember() throws Exception {
        mockMvc.perform(get("/boards/{boardId}/request", BOARD_ID).with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID));
    }

    // ── POST /boards/{boardId}/request ──

    @Test
    @DisplayName("POST /boards/{boardId}/request - 가입 요청 후 리다이렉트")
    void boardRequestPost() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/request", BOARD_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "/request"));

        verify(boardMemberService).save(BOARD_ID, USER_ID, BoardRole.REQUESTED);
    }

    // ── GET /boards/{boardId}/inactive ──

    @Test
    @DisplayName("GET /boards/{boardId}/inactive - 비활성 보드 페이지")
    void boardInactive() throws Exception {
        given(boardService.getBoardDtoById(BOARD_ID))
                .willReturn(new BoardResponseDto(BOARD_ID, "테스트보드", "설명", "p1", "INACTIVE", 3L));
        given(boardMemberService.isManager(BOARD_ID, USER_ID)).willReturn(false);

        mockMvc.perform(get("/boards/{boardId}/inactive", BOARD_ID).with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(view().name("board/inactive"));
    }

    // ── GET /boards/{boardId}/post_list ──

    @Test
    @DisplayName("GET /boards/{boardId}/post_list - 상태별 포스트 목록")
    void postList() throws Exception {
        given(boardService.getBoardDtoById(BOARD_ID)).willReturn(mockBoardDto());

        mockMvc.perform(get("/boards/{boardId}/post_list", BOARD_ID)
                        .param("postStatus", "PROGRESS")
                        .with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(model().attribute("status", "PROGRESS"))
                .andExpect(view().name("board/post_list"));
    }

    // ── POST /boards/create ──

    @Test
    @DisplayName("POST /boards/create - 보드 생성 후 MANAGER 등록")
    void createBoard() throws Exception {
        given(boardService.createBoard(any())).willReturn(BOARD_ID);

        mockMvc.perform(post("/boards/create")
                        .param("name", "새 보드").param("color", "p1")
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));

        verify(boardMemberService).save(BOARD_ID, USER_ID, BoardRole.MANAGER);
    }

    // ── POST /boards/{boardId}/leave ──

    @Test
    @DisplayName("POST /boards/{boardId}/leave - 보드 탈퇴 성공")
    void leaveBoard() throws Exception {
        mockMvc.perform(post("/boards/{boardId}/leave", BOARD_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));

        verify(boardMemberService).leaveBoard(BOARD_ID, USER_ID);
    }

    @Test
    @DisplayName("POST /boards/{boardId}/leave - 마지막 매니저 에러")
    void leaveBoard_lastManager() throws Exception {
        doThrow(new IllegalStateException("last_manager"))
                .when(boardMemberService).leaveBoard(BOARD_ID, USER_ID);

        mockMvc.perform(post("/boards/{boardId}/leave", BOARD_ID)
                        .with(user(mockUser(MemberRole.ROLE_USER))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + BOARD_ID + "?error=last_manager"));
    }

    // ── GET /boards/search ──

    @Test
    @DisplayName("GET /boards/search - 보드 탐색 페이지")
    void searchBoards() throws Exception {
        mockMvc.perform(get("/boards/search").with(user(mockUser(MemberRole.ROLE_USER))))
                .andExpect(status().isOk())
                .andExpect(view().name("board/search"));
    }
}

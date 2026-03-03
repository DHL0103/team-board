package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.AdminController;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private BoardService boardService;

    @MockBean
    private MemberService memberService;

    // ──────────────────────── GET /admin/request ────────────────────────

    @Test
    @DisplayName("요청 목록 페이지 조회 시 admin/request 뷰를 반환한다")
    void requestPage_returnsAdminRequestView() throws Exception {
        Board board = new Board();
        board.setId(1L);
        board.setName("개발팀");

        Post post = new Post();
        post.setId(1L);
        post.setTitle("테스트 게시글");
        post.setStatus("REQUESTED");
        post.setBoardId(1L);
        post.setCreatedAt(LocalDateTime.now());

        given(postService.getRequestedPost()).willReturn(List.of(post));
        given(boardService.getAllBoards()).willReturn(List.of(board));

        mockMvc.perform(get("/admin/request"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/request"))
                .andExpect(model().attributeExists("postList"))
                .andExpect(model().attributeExists("boardPaletteMap"));
    }

    @Test
    @DisplayName("요청 목록이 비어있어도 admin/request 뷰를 반환한다")
    void requestPage_emptyList_returnsAdminRequestView() throws Exception {
        given(postService.getRequestedPost()).willReturn(List.of());
        given(boardService.getAllBoards()).willReturn(List.of());

        mockMvc.perform(get("/admin/request"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/request"));
    }

    // ──────────────────────── POST /admin/approve/{id} ────────────────────────

    @Test
    @DisplayName("게시글 승인 시 status를 APPROVED로 변경하고 /admin/request로 리다이렉트한다")
    void approve_updatesStatusAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/approve/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/request"));

        verify(postService).updateStatus(1L, "APPROVED");
    }

    // ──────────────────────── POST /admin/reject/{id} ────────────────────────

    @Test
    @DisplayName("게시글 반려 시 status를 PROGRESS로 변경하고 /admin/request로 리다이렉트한다")
    void reject_updatesStatusAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/reject/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/request"));

        verify(postService).updateStatus(1L, "PROGRESS");
    }

    // ──────────────────────── GET /admin/members ────────────────────────

    @Test
    @DisplayName("멤버 관리 페이지 조회 시 admin/members 뷰를 반환한다")
    void memberPage_returnsAdminMembersView() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setUsername("user1");
        member.setRole("ROLE_USER");

        Board board = new Board();
        board.setId(1L);
        board.setName("개발팀");

        given(memberService.getAllMemberExceptAdmin()).willReturn(List.of(member));
        given(boardService.getAllBoards()).willReturn(List.of(board));

        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/members"))
                .andExpect(model().attributeExists("memberList"))
                .andExpect(model().attributeExists("boardList"))
                .andExpect(model().attributeExists("boardMap"));
    }

    // ──────────────────────── GET /admin/boards ────────────────────────

    @Test
    @DisplayName("보드 관리 페이지 조회 시 admin/boards 뷰를 반환한다")
    void boardsPage_returnsAdminBoardsView() throws Exception {
        Board board = new Board();
        board.setId(1L);
        board.setName("개발팀");

        given(boardService.getAllBoards()).willReturn(List.of(board));

        mockMvc.perform(get("/admin/boards"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/boards"))
                .andExpect(model().attributeExists("boardList"));
    }

    // ──────────────────────── POST /admin/board/create ────────────────────────

    @Test
    @DisplayName("보드 생성 후 /admin/boards로 리다이렉트한다")
    void createBoard_redirectsToBoards() throws Exception {
        mockMvc.perform(post("/admin/board/create")
                        .param("name", "신규팀")
                        .param("slug", "new-team")
                        .param("description", "신규 팀 설명"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/boards"));

        verify(boardService).createBoard(org.mockito.ArgumentMatchers.any());
    }

    // ──────────────────────── POST /admin/board/update/{id} ────────────────────────

    @Test
    @DisplayName("보드 수정 후 /admin/boards로 리다이렉트한다")
    void updateBoard_redirectsToBoards() throws Exception {
        mockMvc.perform(post("/admin/board/update/1")
                        .param("name", "수정팀")
                        .param("slug", "updated-team")
                        .param("description", "수정된 설명"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/boards"));

        verify(boardService).updateBoard(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any());
    }

    // ──────────────────────── POST /admin/board/delete/{id} ────────────────────────

    @Test
    @DisplayName("보드 삭제 시 해당 보드의 게시글도 삭제하고 /admin/boards로 리다이렉트한다")
    void deleteBoard_deletesPostsAndRedirects() throws Exception {
        Post post = new Post();
        post.setId(10L);
        post.setBoardId(1L);

        given(postService.getPostsByBoardId(1L)).willReturn(List.of(post));

        mockMvc.perform(post("/admin/board/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/boards"));

        verify(postService).deletePost(post);
        verify(boardService).deleteBoard(1L);
    }

    // ──────────────────────── POST /admin/member/create ────────────────────────

    @Test
    @DisplayName("멤버 생성 후 /admin/members로 리다이렉트한다")
    void createMember_redirectsToMembers() throws Exception {
        mockMvc.perform(post("/admin/member/create")
                        .param("username", "newUser")
                        .param("password", "password123")
                        .param("boardId", "1")
                        .param("role", "ROLE_USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/members"));

        verify(memberService).create(org.mockito.ArgumentMatchers.any());
    }

    // ──────────────────────── POST /admin/member/update/{id} ────────────────────────

    @Test
    @DisplayName("멤버 수정 후 /admin/members로 리다이렉트한다")
    void updateMember_redirectsToMembers() throws Exception {
        mockMvc.perform(post("/admin/member/update/1")
                        .param("role", "ROLE_ADMIN")
                        .param("boardId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/members"));

        verify(memberService).update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any());
    }

    // ──────────────────────── POST /admin/member/delete/{id} ────────────────────────

    @Test
    @DisplayName("멤버 삭제 시 해당 멤버의 게시글도 삭제하고 /admin/members로 리다이렉트한다")
    void deleteMember_deletesPostsAndRedirects() throws Exception {
        Post post = new Post();
        post.setId(20L);
        post.setMemberId(1L);

        given(postService.getPostsByMemberId(1L)).willReturn(List.of(post));

        mockMvc.perform(post("/admin/member/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/members"));

        verify(postService).deletePost(post);
        verify(memberService).delete(1L);
    }
}
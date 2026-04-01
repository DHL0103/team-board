package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.AdminBoardController;
import kr.co.promptech.springboottutorial.controller.AdminMemberController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import kr.co.promptech.springboottutorial.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({AdminBoardController.class, AdminMemberController.class})
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private CustomUser adminUser() {
        return new CustomUser(1L, "admin", "pw", MemberRole.ROLE_ADMIN,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private CustomUser normalUser() {
        return new CustomUser(2L, "user", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /admin/boards - ADMIN 접근 가능")
    void adminBoards_admin() throws Exception {
        mockMvc.perform(get("/admin/boards").with(user(adminUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/boards"));
    }

    @Test
    @DisplayName("GET /admin/boards - 일반 유저 403")
    void adminBoards_user_forbidden() throws Exception {
        mockMvc.perform(get("/admin/boards").with(user(normalUser())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /admin/members - ADMIN 접근 가능")
    void adminMembers_admin() throws Exception {
        mockMvc.perform(get("/admin/members").with(user(adminUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/members"));
    }

    @Test
    @DisplayName("GET /admin/members - 일반 유저 403")
    void adminMembers_user_forbidden() throws Exception {
        mockMvc.perform(get("/admin/members").with(user(normalUser())))
                .andExpect(status().isForbidden());
    }
}

package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.MemberController;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.MemberService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import kr.co.promptech.springboottutorial.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class)
class MemberControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private MemberService memberService;
    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    @Test
    @DisplayName("GET /members/login - 로그인 페이지")
    void loginPage() throws Exception {
        mockMvc.perform(get("/members/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login_form"));
    }

    @Test
    @DisplayName("GET /members/signup - 회원가입 페이지")
    void signupPage() throws Exception {
        mockMvc.perform(get("/members/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    @DisplayName("POST /members/signup - 성공 시 로그인으로 리다이렉트")
    void signup_success() throws Exception {
        given(memberService.signup("testuser", "pass1234")).willReturn(true);

        mockMvc.perform(post("/members/signup")
                        .param("username", "testuser")
                        .param("password", "pass1234")
                        .param("passwordConfirm", "pass1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/login?registered=true"));

        verify(memberService).signup("testuser", "pass1234");
    }

    @Test
    @DisplayName("POST /members/signup - 비밀번호 불일치 시 signup 뷰 반환")
    void signup_passwordMismatch() throws Exception {
        mockMvc.perform(post("/members/signup")
                        .param("username", "testuser")
                        .param("password", "pass1234")
                        .param("passwordConfirm", "different")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    @DisplayName("POST /members/signup - 아이디 중복 시 signup 뷰 반환")
    void signup_duplicateUsername() throws Exception {
        given(memberService.signup("testuser", "pass1234")).willReturn(false);

        mockMvc.perform(post("/members/signup")
                        .param("username", "testuser")
                        .param("password", "pass1234")
                        .param("passwordConfirm", "pass1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    @DisplayName("POST /members/signup - 유효성 검사 실패 시 signup 뷰 반환")
    void signup_validationError() throws Exception {
        mockMvc.perform(post("/members/signup")
                        .param("username", "ab")   // 4자 미만
                        .param("password", "pw")    // 4자 미만
                        .param("passwordConfirm", "pw")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }
}

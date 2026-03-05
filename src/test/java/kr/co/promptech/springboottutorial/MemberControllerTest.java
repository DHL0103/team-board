package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.MemberController;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("로그인 페이지 요청 시 login_form 뷰를 반환한다")
    void loginPage_returnsLoginForm() throws Exception {
        mockMvc.perform(get("/member/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login_form"));
    }

    @Test
    @DisplayName("존재하는 멤버 ID로 조회 시 Member JSON을 반환한다")
    void getMember_returnsMemberJson() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setUsername("testUser");
        member.setRole("ROLE_USER");
        member.setBoardId(1L);

        given(memberService.getMemberById(1L)).willReturn(member);

        mockMvc.perform(get("/member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("존재하지 않는 멤버 ID 조회 시 null을 반환한다")
    void getMember_notFound_returnsNull() throws Exception {
        given(memberService.getMemberById(999L)).willReturn(null);

        mockMvc.perform(get("/member/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}

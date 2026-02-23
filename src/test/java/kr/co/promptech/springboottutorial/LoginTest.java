package kr.co.promptech.springboottutorial;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest // 시큐리티 필터를 포함한 전체 컨텍스트 로드
@AutoConfigureMockMvc
class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그인 성공 시 메인 페이지로 리다이렉트된다")
    void login_Success() throws Exception {
        mockMvc.perform(formLogin("/member/login") // 시큐리티 로그인 주소
                        .user("username", "daehee_fe")      // 폼 데이터 key 이름에 맞춰서 입력
                        .password("password", "1234"))
                .andExpect(status().is3xxRedirection()) // 성공 시 리다이렉트 여부 확인
                .andExpect(redirectedUrl("/board"));         // 성공 후 이동할 경로
    }

    @Test
    @DisplayName("로그인 실패 시 에러 파라미터와 함께 로그인 페이지로 리다이렉트된다")
    void login_Fail() throws Exception {
        mockMvc.perform(formLogin("/member/login")
                        .user("username", "wrongUser")
                        .password("password", "wrongPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login?error=true")); // 아까 설정한 실패 경로 확인
    }
}

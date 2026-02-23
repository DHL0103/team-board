package kr.co.promptech.springboottutorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.promptech.springboottutorial.dto.MemberCreateDto;
import kr.co.promptech.springboottutorial.member.MemberController;
import kr.co.promptech.springboottutorial.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // 수정됨
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // 수정됨
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class) // 특정 컨트롤러만 로드하여 테스트
@AutoConfigureMockMvc(addFilters = false) // Spring Security 필터 비활성화 (테스트 편의상)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService; // 컨트롤러가 의존하는 서비스는 Mock으로 대체

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 요청 시 성공 메시지를 반환한다")
    void signup_Success() throws Exception {
        // given: 테스트 데이터 준비
        MemberCreateDto dto = new MemberCreateDto();
        dto.setUsername("testUser");
        dto.setPassword("password123!");
        dto.setBoardId(1L);

        // when & then: 요청을 보내고 결과 검증
        mockMvc.perform(post("/member/signup") // 여기에 @RequestMapping 경로가 있다면 맞춰주세요 (예: /member/signup)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입 성공"));

        // 서비스의 create 메서드가 실제로 호출되었는지 확인
        verify(memberService).create(anyString(), anyString(), any());
    }

}

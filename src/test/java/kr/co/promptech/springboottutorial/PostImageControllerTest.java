package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.PostImageController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.BoardService;
import kr.co.promptech.springboottutorial.service.PostFileService;
import kr.co.promptech.springboottutorial.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostImageController.class)
class PostImageControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostFileService postFileService;
    @MockBean private BoardService boardService;
    @MockBean private BoardMemberService boardMemberService;
    @MockBean private PostService postService;
    @MockBean private UserDetailsService userDetailsService;

    private CustomUser mockUser() {
        return new CustomUser(1L, "user", "pw", MemberRole.ROLE_USER,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("POST /posts/image - 인라인 이미지 업로드 성공")
    void uploadImage_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", new byte[1024]);

        given(postFileService.saveInlineImage(any())).willReturn("/files/uuid-test.png");

        mockMvc.perform(multipart("/posts/image")
                        .file(file)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/files/uuid-test.png"));
    }

    @Test
    @DisplayName("POST /posts/image - 5MB 초과 시 400")
    void uploadImage_tooLarge() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.png", "image/png", new byte[6 * 1024 * 1024]);

        mockMvc.perform(multipart("/posts/image")
                        .file(file)
                        .with(user(mockUser())).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}

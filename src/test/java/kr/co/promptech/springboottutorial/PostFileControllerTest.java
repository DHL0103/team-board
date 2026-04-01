package kr.co.promptech.springboottutorial;

import kr.co.promptech.springboottutorial.controller.PostFileController;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.FileDownloadDto;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostFileController.class)
class PostFileControllerTest {

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
    @DisplayName("GET /files/{filename} - 파일 다운로드 성공")
    void serveFile_success() throws Exception {
        byte[] content = "file-content".getBytes();
        FileDownloadDto dto = new FileDownloadDto(
                new ByteArrayResource(content), "application/pdf", "test.pdf");

        given(postFileService.getFileForDownload("abc-uuid.pdf", "test.pdf")).willReturn(dto);

        mockMvc.perform(get("/files/abc-uuid.pdf")
                        .param("name", "test.pdf")
                        .with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().exists("Content-Disposition"));
    }

    @Test
    @DisplayName("GET /files/{filename} - 파일 없으면 404")
    void serveFile_notFound() throws Exception {
        given(postFileService.getFileForDownload("no-file.pdf", null)).willReturn(null);

        mockMvc.perform(get("/files/no-file.pdf")
                        .with(user(mockUser())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /files/{filename} - name 없이 인라인 다운로드")
    void serveFile_inline() throws Exception {
        byte[] content = "img-content".getBytes();
        FileDownloadDto dto = new FileDownloadDto(
                new ByteArrayResource(content), "image/png", "uuid-img.png");

        given(postFileService.getFileForDownload("uuid-img.png", null)).willReturn(dto);

        mockMvc.perform(get("/files/uuid-img.png")
                        .with(user(mockUser())))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }
}

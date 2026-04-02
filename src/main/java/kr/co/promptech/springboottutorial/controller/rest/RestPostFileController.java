package kr.co.promptech.springboottutorial.controller.rest;

import kr.co.promptech.springboottutorial.model.dto.FileDownloadDto;
import kr.co.promptech.springboottutorial.service.PostFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class RestPostFileController {

    private final PostFileService postFileService;

    /**
     * @param filename 서버에 저장된 파일명 (UUID 기반)
     * @param name     다운로드 시 표시할 원본 파일명 (optional)
     * @return 파일 리소스 응답 (Content-Disposition: attachment)
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> serveFile(@PathVariable String filename,
                                       @RequestParam(required = false) String name) throws IOException {
        FileDownloadDto file = postFileService.getFileForDownload(filename, name);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        ContentDisposition contentDisposition = (name != null)
                ? ContentDisposition.attachment().filename(file.getDisplayName(), StandardCharsets.UTF_8).build()
                : ContentDisposition.inline().build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(file.getResource());
    }
}

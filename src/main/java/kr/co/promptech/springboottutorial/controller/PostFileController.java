package kr.co.promptech.springboottutorial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class PostFileController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    /**
     * @param filename 서버에 저장된 파일명 (UUID 기반)
     * @param name     다운로드 시 표시할 원본 파일명 (optional)
     * @return 파일 리소스 응답 (Content-Disposition: attachment)
     * 업로드된 파일을 다운로드. name 파라미터로 원본 파일명 복원, 없으면 저장 파일명 사용
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename,
                                              @RequestParam(required = false) String name) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String displayName = (name != null && !name.isBlank()) ? name : filename;
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(displayName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }
}
package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.service.PostFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/posts/image")
@RequiredArgsConstructor
public class PostImageController {

    private final PostFileService postFileService;

    private static final long INLINE_MAX_SIZE = 5L * 1024 * 1024;

    /**
     * 에디터 본문 내 인라인 이미지 업로드 처리
     * @param file 업로드할 인라인 이미지 파일 (최대 5MB)
     * @return 성공 시 업로드된 이미지 URL (JSON), 초과 시 400 에러
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadInlineImage(
            @RequestParam("file") MultipartFile file) {
        if (file.getSize() > INLINE_MAX_SIZE) {
            return ResponseEntity.badRequest().body(Map.of("error", "인라인 이미지 크기는 5MB를 초과할 수 없습니다."));
        }
        String url = postFileService.saveInlineImage(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}

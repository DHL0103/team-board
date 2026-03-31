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
@RequestMapping("/post/image")
@RequiredArgsConstructor
public class PostImageController {

    private final PostFileService postFileService;

    private static final long INLINE_MAX_SIZE = 5L * 1024 * 1024;

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

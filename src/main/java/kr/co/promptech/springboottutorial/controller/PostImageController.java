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

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadInlineImage(
            @RequestParam("file") MultipartFile file) {
        String url = postFileService.saveInlineImage(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}

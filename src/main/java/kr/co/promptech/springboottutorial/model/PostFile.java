package kr.co.promptech.springboottutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostFile {
    private Long id;
    private Long postId;
    private String originalName;
    private String storedPath;
    private Long fileSize;
    private LocalDateTime createdAt;
}

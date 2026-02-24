package kr.co.promptech.springboottutorial.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostFile {
    private Long id;
    private Long postId;
    private String originalName;
    private String storedPath;
    private Long fileSize;
    private LocalDateTime createdAt;
}

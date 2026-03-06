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
    private Long postId;            // 첨부 대상 업무 ID (FK → posts.id)
    private String originalName;    // 업로드 원본 파일명
    private String storedPath;      // 서버 저장 경로 (UUID 기반 파일명)
    private Long fileSize;          // 파일 크기 (bytes)
    private LocalDateTime createdAt; // 업로드 일시
}

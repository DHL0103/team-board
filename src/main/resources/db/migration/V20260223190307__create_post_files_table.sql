CREATE TABLE post_files (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '파일 고유 ID',
    post_id       BIGINT NOT NULL                   COMMENT '대상 게시글 ID (FK → posts.id)',
    original_name VARCHAR(255) NOT NULL             COMMENT '업로드 원본 파일명',
    stored_path   VARCHAR(255) NOT NULL             COMMENT '서버 저장 경로',
    file_size     BIGINT NOT NULL                   COMMENT '파일 크기 (bytes)',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '업로드 일시',
    CONSTRAINT fk_file_post FOREIGN KEY (post_id) REFERENCES posts(id)
);

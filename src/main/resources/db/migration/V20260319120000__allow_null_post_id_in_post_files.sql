ALTER TABLE post_files
    DROP FOREIGN KEY fk_file_post;

ALTER TABLE post_files
    MODIFY COLUMN post_id BIGINT NULL COMMENT '대상 게시글 ID (FK → posts.id, 인라인 이미지 업로드 중 임시 null 허용)';

ALTER TABLE post_files
    ADD CONSTRAINT fk_file_post FOREIGN KEY (post_id) REFERENCES posts(id);

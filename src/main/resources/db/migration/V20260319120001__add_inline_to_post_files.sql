ALTER TABLE post_files
    ADD COLUMN inline TINYINT(1) NOT NULL DEFAULT 0 COMMENT '인라인 이미지 여부 (1=인라인, 0=첨부파일)';

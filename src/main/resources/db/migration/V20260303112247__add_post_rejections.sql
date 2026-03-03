-- 6. 반려 사유 테이블 (게시글 참조, CASCADE DELETE)
CREATE TABLE IF NOT EXISTS post_rejections (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY  COMMENT '반려 고유 ID',
    post_id     BIGINT NOT NULL                    COMMENT '대상 게시글 ID (FK → posts.id)',
    reason      TEXT NOT NULL                      COMMENT '반려 사유',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '반려 일시',
    CONSTRAINT fk_rejection_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
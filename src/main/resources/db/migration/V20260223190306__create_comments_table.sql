CREATE TABLE comments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY  COMMENT '댓글 고유 ID',
    post_id     BIGINT NOT NULL                    COMMENT '대상 게시글 ID (FK → posts.id)',
    member_id   BIGINT NOT NULL                    COMMENT '작성자 ID (FK → members.id)',
    content     TEXT NOT NULL                      COMMENT '댓글 내용',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
    CONSTRAINT fk_comment_post   FOREIGN KEY (post_id)   REFERENCES posts(id),
    CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE post_members (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id   BIGINT NOT NULL COMMENT '대상 게시글 ID',
    member_id BIGINT NOT NULL COMMENT '담당자로 지정된 회원 ID',
    UNIQUE KEY uk_post_member (post_id, member_id),
    CONSTRAINT fk_pm_post   FOREIGN KEY (post_id)   REFERENCES posts(id)   ON DELETE CASCADE,
    CONSTRAINT fk_pm_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

ALTER TABLE comments
    ADD COLUMN parent_id  BIGINT  NULL                   COMMENT '부모 댓글 ID',
    ADD COLUMN group_id   BIGINT  NULL                   COMMENT '최상위 부모 ID (정렬용)',
    ADD COLUMN depth      INT     NOT NULL DEFAULT 0     COMMENT '계층 깊이',
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '논리적 삭제 여부';

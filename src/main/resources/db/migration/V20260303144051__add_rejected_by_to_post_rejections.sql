ALTER TABLE post_rejections
    ADD COLUMN rejected_by BIGINT COMMENT '반려한 관리자 ID (FK → members.id)',
    ADD CONSTRAINT fk_rejection_member FOREIGN KEY (rejected_by) REFERENCES members(id) ON DELETE SET NULL;
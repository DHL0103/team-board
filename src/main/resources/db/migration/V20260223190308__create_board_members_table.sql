CREATE TABLE board_members (
    id         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    board_id   BIGINT      NOT NULL,
    member_id  BIGINT      NOT NULL,
    board_role VARCHAR(50) NOT NULL DEFAULT 'REQUESTED' COMMENT 'REQUESTED / USER / MANAGER',
    UNIQUE KEY uk_board_member (board_id, member_id),
    CONSTRAINT fk_bm_board  FOREIGN KEY (board_id)  REFERENCES boards(id)  ON DELETE CASCADE,
    CONSTRAINT fk_bm_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

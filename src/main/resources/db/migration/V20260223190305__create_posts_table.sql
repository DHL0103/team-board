CREATE TABLE posts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY  COMMENT '게시글 고유 ID',
    board_id    BIGINT NOT NULL                    COMMENT '소속 게시판 ID (FK → boards.id)',
    member_id   BIGINT NOT NULL                    COMMENT '작성자 ID (FK → members.id)',
    title       VARCHAR(255) NOT NULL              COMMENT '게시글 제목',
    content     TEXT NOT NULL                      COMMENT '게시글 본문',
    status      VARCHAR(255) NOT NULL DEFAULT 'REQUESTED' COMMENT '작업 상태 (PROGRESS / REQUESTED / COMPLETED)',
    due_date    DATETIME                           COMMENT '마감일 (선택)',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
    updated_at  DATETIME ON UPDATE CURRENT_TIMESTAMP        COMMENT '수정일시 (자동 갱신)',
    CONSTRAINT fk_post_board   FOREIGN KEY (board_id)  REFERENCES boards(id),
    CONSTRAINT fk_post_member  FOREIGN KEY (member_id) REFERENCES members(id)
);

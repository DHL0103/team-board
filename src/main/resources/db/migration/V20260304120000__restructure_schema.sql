-- ① boards: slug 제거, color / status 추가
ALTER TABLE boards
    DROP INDEX slug,
    DROP COLUMN slug,
    ADD COLUMN color  VARCHAR(255) NOT NULL DEFAULT 'p1'     COMMENT '테마 색상 (p1~p6)',
    ADD COLUMN status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE' COMMENT '게시판 상태';

-- ② board_members 생성
CREATE TABLE board_members (
    id         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    board_id   BIGINT      NOT NULL,
    member_id  BIGINT      NOT NULL,
    board_role VARCHAR(50) NOT NULL DEFAULT 'REQUESTED' COMMENT 'REQUESTED / USER / MANAGER',
    UNIQUE KEY uk_board_member (board_id, member_id),
    CONSTRAINT fk_bm_board  FOREIGN KEY (board_id)  REFERENCES boards(id)  ON DELETE CASCADE,
    CONSTRAINT fk_bm_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- ③ 기존 members.board_id 데이터를 board_members로 이전
INSERT INTO board_members (board_id, member_id, board_role)
SELECT board_id, id, 'USER'
FROM members
WHERE board_id IS NOT NULL;

-- ④ members: board_id FK / 컬럼 제거
ALTER TABLE members
    DROP FOREIGN KEY fk_member_board,
    DROP COLUMN board_id;

-- ⑤ post_members (담당자) 생성
CREATE TABLE post_members (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id   BIGINT NOT NULL COMMENT '대상 게시글 ID',
    member_id BIGINT NOT NULL COMMENT '담당자로 지정된 회원 ID',
    UNIQUE KEY uk_post_member (post_id, member_id),
    CONSTRAINT fk_pm_post   FOREIGN KEY (post_id)   REFERENCES posts(id)   ON DELETE CASCADE,
    CONSTRAINT fk_pm_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

-- ⑥ comments: 계층형 컬럼 추가
ALTER TABLE comments
    ADD COLUMN parent_id  BIGINT  NULL                    COMMENT '부모 댓글 ID',
    ADD COLUMN group_id   BIGINT  NULL                    COMMENT '최상위 부모 ID (정렬용)',
    ADD COLUMN depth      INT     NOT NULL DEFAULT 0      COMMENT '계층 깊이',
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE  COMMENT '논리적 삭제 여부';
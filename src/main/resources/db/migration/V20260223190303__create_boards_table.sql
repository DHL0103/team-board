CREATE TABLE boards (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시판 고유 ID',
    name        VARCHAR(255) NOT NULL              COMMENT '게시판 이름 (예: Backend팀)',
    description TEXT                               COMMENT '게시판 설명',
    color       VARCHAR(255) NOT NULL DEFAULT 'p1' COMMENT '테마 색상 (p1~p6)',
    status      VARCHAR(255) NOT NULL DEFAULT 'ACTIVE' COMMENT '게시판 상태'
);

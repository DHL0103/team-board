-- 1. 게시판 테이블 (상위)
CREATE TABLE boards (
                        id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시판 고유 ID',
                        name        VARCHAR(255) NOT NULL              COMMENT '게시판 이름 (예: Backend팀)',
                        slug        VARCHAR(255) NOT NULL UNIQUE       COMMENT 'URL용 식별자 (예: backend)',
                        description TEXT                               COMMENT '게시판 설명'
);

-- 2. 회원 테이블 (게시판 참조 가능)
CREATE TABLE members (
                         id          BIGINT AUTO_INCREMENT PRIMARY KEY  COMMENT '회원 고유 ID',
                         username    VARCHAR(255) NOT NULL UNIQUE       COMMENT '로그인 아이디',
                         password    VARCHAR(255) NOT NULL              COMMENT '암호화된 비밀번호',
                         role        VARCHAR(255) NOT NULL              COMMENT '권한 (ROLE_USER / ROLE_ADMIN)',
                         board_id    BIGINT                             COMMENT '소속 게시판 ID (FK → boards.id)',
                         CONSTRAINT fk_member_board FOREIGN KEY (board_id) REFERENCES boards(id)
);

-- 3. 게시글 테이블 (게시판, 회원 참조)
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

-- 4. 댓글 테이블 (게시글, 회원 참조)
CREATE TABLE comments (
                          id          BIGINT AUTO_INCREMENT PRIMARY KEY  COMMENT '댓글 고유 ID',
                          post_id     BIGINT NOT NULL                    COMMENT '대상 게시글 ID (FK → posts.id)',
                          member_id   BIGINT NOT NULL                    COMMENT '작성자 ID (FK → members.id)',
                          content     TEXT NOT NULL                      COMMENT '댓글 내용',
                          created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
                          CONSTRAINT fk_comment_post   FOREIGN KEY (post_id)   REFERENCES posts(id),
                          CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES members(id)
);

-- 5. 파일 첨부 테이블 (게시글 참조)
CREATE TABLE post_files (
                            id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '파일 고유 ID',
                            post_id       BIGINT NOT NULL                   COMMENT '대상 게시글 ID (FK → posts.id)',
                            original_name VARCHAR(255) NOT NULL             COMMENT '업로드 원본 파일명',
                            stored_path   VARCHAR(255) NOT NULL             COMMENT '서버 저장 경로',
                            file_size     BIGINT NOT NULL                   COMMENT '파일 크기 (bytes)',
                            created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '업로드 일시',
                            CONSTRAINT fk_file_post FOREIGN KEY (post_id) REFERENCES posts(id)
);
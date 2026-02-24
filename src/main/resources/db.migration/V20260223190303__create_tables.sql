-- 1. 게시판 테이블 (상위)
CREATE TABLE boards (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        slug VARCHAR(255) NOT NULL UNIQUE,
                        description TEXT
);

-- 2. 회원 테이블 (게시판 참조 가능)
CREATE TABLE members (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(255) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         role VARCHAR(255) NOT NULL,
                         board_id BIGINT,
                         CONSTRAINT fk_member_board FOREIGN KEY (board_id) REFERENCES boards(id)
);

-- 3. 게시글 테이블 (게시판, 회원 참조)
CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       board_id BIGINT NOT NULL,
                       member_id BIGINT NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       status VARCHAR(255) NOT NULL DEFAULT 'REQUESTED',
                       due_date DATETIME,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
                       CONSTRAINT fk_post_board FOREIGN KEY (board_id) REFERENCES boards(id),
                       CONSTRAINT fk_post_member FOREIGN KEY (member_id) REFERENCES members(id)
);

-- 4. 댓글 테이블 (게시글, 회원 참조)
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          post_id BIGINT NOT NULL,
                          member_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id),
                          CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES members(id)
);

-- 5. 파일 첨부 테이블 (게시글 참조)
CREATE TABLE post_files (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            post_id BIGINT NOT NULL,
                            original_name VARCHAR(255) NOT NULL,
                            stored_path VARCHAR(255) NOT NULL,
                            file_size BIGINT NOT NULL,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_file_post FOREIGN KEY (post_id) REFERENCES posts(id)
);
CREATE TABLE members (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '회원 고유 ID',
    username VARCHAR(255) NOT NULL UNIQUE      COMMENT '로그인 아이디',
    password VARCHAR(255) NOT NULL             COMMENT '암호화된 비밀번호',
    role     VARCHAR(255) NOT NULL             COMMENT '권한 (ROLE_USER / ROLE_ADMIN)'
);

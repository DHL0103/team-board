
-- 1. Boards 데이터
INSERT INTO boards (name, slug, description) VALUES
                                                 ('Backend', 'backend', '서버 개발 및 API 인프라 구축'),
                                                 ('Frontend', 'frontend', '사용자 화면 및 인터렉션 구현'),
                                                 ('Design', 'design', 'UI/UX 디자인 및 가이드라인 제작');

-- 2. Members 데이터
INSERT INTO members (username, password, `role`, board_id) VALUES
                                                               ('daehee_be',   '1234', 'ROLE_USER', 1),
                                                               ('chulsu_fe',   '1234', 'ROLE_USER', 2),
                                                               ('younghee_de', '1234', 'ROLE_USER', 3),
                                                               ('boss_master', '1234', 'ROLE_ADMIN', NULL),
                                                               ('daehee_fe',   '$2a$10$QZmfaj7yvHAseDZuYrzlOOSFr04O7dgYMtkWaC7.ynSKJaWbEAmBG', 'ROLE_USER', 2),
                                                               ('test_fe',     '$2a$10$coUdPEoZcJ/gJXe3QGjj/umBUOpQJsMC5svEaTWlNBFZ9EDshklbK', 'ROLE_USER', 2),
                                                               ('test_be',     '$2a$10$DGyJ1qXE1zmCasGgZoVp3OhVYAjyGaea32b6SVlLjqo7eZOUTL0aq', 'ROLE_USER', 1),
                                                               ('test_de',     '$2a$10$3I6hVY5tcjqm8vE8GINBnuSlWBMM00/7tCSxhJKWgQh8Yr3CxajoC', 'ROLE_USER', 3);

-- 3. Posts 데이터
INSERT INTO posts (board_id, member_id, title, content, status, due_date, created_at, updated_at) VALUES
                                                                                                      (1, 1, '서버 보안 취약점 점검', '긴급 보안 패치 적용 필요.', 'PROGRESS', '2026-02-21 23:59:59', '2026-02-20 06:09:50', NULL),
                                                                                                      (2, 2, '메인 페이지 타임리프 레이아웃', 'GNB 및 푸터 레이아웃 작업 진행 중입니다.', 'PROGRESS', '2026-02-25 10:00:00', '2026-02-20 06:21:13', NULL),
                                                                                                      (3, 3, '신규 로고 시안 확정', '최종 로고 컬러 가이드 전달 완료.', 'REQUESTED', '2026-02-15 14:00:00', '2026-02-20 06:21:46', NULL),
                                                                                                      (3, 8, '디자인 피드백', '해주세요', 'PROGRESS', '2026-03-11 00:00:00', '2026-02-23 12:53:02', NULL);
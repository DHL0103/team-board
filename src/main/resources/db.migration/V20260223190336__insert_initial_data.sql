
-- 1. Boards 데이터
INSERT INTO boards (name, slug, description) VALUES
                                                 ('Backend', 'backend', '서버 개발 및 API 인프라 구축'),
                                                 ('Frontend', 'frontend', '사용자 화면 및 인터렉션 구현'),
                                                 ('Design', 'design', 'UI/UX 디자인 및 가이드라인 제작');

-- 2. Members 데이터
INSERT INTO members (username, password, `role`, board_id) VALUES
                                                               ('admin', '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_ADMIN', NULL),
                                                               ('daehee_fe',   '$2a$10$QZmfaj7yvHAseDZuYrzlOOSFr04O7dgYMtkWaC7.ynSKJaWbEAmBG', 'ROLE_USER', 2),
                                                               ('test_fe',     '$2a$10$coUdPEoZcJ/gJXe3QGjj/umBUOpQJsMC5svEaTWlNBFZ9EDshklbK', 'ROLE_USER', 2),
                                                               ('test_be',     '$2a$10$DGyJ1qXE1zmCasGgZoVp3OhVYAjyGaea32b6SVlLjqo7eZOUTL0aq', 'ROLE_USER', 1),
                                                               ('test_de',     '$2a$10$3I6hVY5tcjqm8vE8GINBnuSlWBMM00/7tCSxhJKWgQh8Yr3CxajoC', 'ROLE_USER', 3);
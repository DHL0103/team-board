-- 1. Boards 데이터
INSERT INTO boards (name, description, color) VALUES
    ('Backend',  '서버 개발 및 API 인프라 구축',    'p2'),
    ('Frontend', '사용자 화면 및 인터렉션 구현',      'p4'),
    ('Design',   'UI/UX 디자인 및 가이드라인 제작',   'p1');

-- 2. Members 데이터
INSERT INTO members (username, password, `role`) VALUES
    ('admin',    '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_ADMIN'),
    ('daehee_fe','$2a$10$QZmfaj7yvHAseDZuYrzlOOSFr04O7dgYMtkWaC7.ynSKJaWbEAmBG', 'ROLE_USER'),
    ('test_fe',  '$2a$10$coUdPEoZcJ/gJXe3QGjj/umBUOpQJsMC5svEaTWlNBFZ9EDshklbK', 'ROLE_USER'),
    ('test_be',  '$2a$10$DGyJ1qXE1zmCasGgZoVp3OhVYAjyGaea32b6SVlLjqo7eZOUTL0aq', 'ROLE_USER'),
    ('test_de',  '$2a$10$3I6hVY5tcjqm8vE8GINBnuSlWBMM00/7tCSxhJKWgQh8Yr3CxajoC', 'ROLE_USER');

-- 3. Board Members 데이터 (board_id: 1=Backend, 2=Frontend, 3=Design / member_id: 2=daehee_fe, 3=test_fe, 4=test_be, 5=test_de)
INSERT INTO board_members (board_id, member_id, board_role) VALUES
    (1, 4, 'USER'),  -- Backend  ← test_be
    (2, 2, 'USER'),  -- Frontend ← daehee_fe
    (2, 3, 'USER'),  -- Frontend ← test_fe
    (3, 5, 'USER');  -- Design   ← test_de

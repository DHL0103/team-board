-- =====================================================
-- Board 4: Mobile, Board 5: Data 추가
-- 각 보드에 매니저 1명 + 유저 2명
-- 비밀번호: 1234
-- =====================================================

-- 1. 보드 추가
INSERT INTO boards (name, description, color) VALUES
    ('Mobile',  '모바일 앱 개발 및 모바일 웹 최적화', 'p3'),
    ('Data',    '데이터 분석 및 지표 관리',           'p5');

-- 2. 멤버 추가 (id=9~14)
INSERT INTO members (username, password, `role`) VALUES
    ('mobile_manager', '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_USER'),  -- id=9
    ('data_manager',   '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_USER'),  -- id=10
    ('test_mobile1',   '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_USER'),  -- id=11
    ('test_mobile2',   '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_USER'),  -- id=12
    ('test_data1',     '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_USER'),  -- id=13
    ('test_data2',     '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_USER');  -- id=14

-- 3. board_members 배정
-- board_id=4: Mobile / board_id=5: Data
INSERT INTO board_members (board_id, member_id, board_role) VALUES
    (4, 9,  'MANAGER'),  -- Mobile  ← mobile_manager
    (4, 11, 'USER'),     -- Mobile  ← test_mobile1
    (4, 12, 'USER'),     -- Mobile  ← test_mobile2
    (5, 10, 'MANAGER'),  -- Data    ← data_manager
    (5, 13, 'USER'),     -- Data    ← test_data1
    (5, 14, 'USER');     -- Data    ← test_data2
-- board별 MANAGER 계정 추가
-- 비밀번호: 1234
INSERT INTO members (username, password, `role`) VALUES
    ('be_manager', '$2a$10$mF3mNwbiv7uF9VpsmRlw0ujAQ48QfXsSHHzwSLCLcG2u0tfdON6we', 'ROLE_USER'),  -- id=6
    ('fe_manager', '$2a$10$QZmfaj7yvHAseDZuYrzlOOSFr04O7dgYMtkWaC7.ynSKJaWbEAmBG', 'ROLE_USER'),  -- id=7
    ('de_manager', '$2a$10$coUdPEoZcJ/gJXe3QGjj/umBUOpQJsMC5svEaTWlNBFZ9EDshklbK', 'ROLE_USER');  -- id=8

-- board_members에 MANAGER 권한으로 배정
INSERT INTO board_members (board_id, member_id, board_role) VALUES
    (1, 6, 'MANAGER'),  -- Backend  ← be_manager
    (2, 7, 'MANAGER'),  -- Frontend ← fe_manager
    (3, 8, 'MANAGER');  -- Design   ← de_manager
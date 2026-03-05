-- 1. 게시판 색상 설정 (Backend, Frontend)
UPDATE boards SET color = 'p2' WHERE id = 1; -- Backend
UPDATE boards SET color = 'p4' WHERE id = 2; -- Frontend

-- 2. daehee_fe(id=2)를 Backend 게시판에도 추가
INSERT INTO board_members (board_id, member_id, board_role)
VALUES (1, 2, 'USER');

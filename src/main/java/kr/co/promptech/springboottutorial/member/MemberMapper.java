package kr.co.promptech.springboottutorial.member;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
interface MemberMapper {
    @Select("SELECT * FROM member WHERE id = #{id}")
    Member selectMemberById(Long id);

    @Insert("INSERT INTO member (username, password, role, board_id) VALUES (#{username}, #{password}, #{role}, #{boardId})")
    void save(Member member);

    @Select("SELECT * FROM member WHERE username = #{username}")
    Member findByUsername(String username);
}

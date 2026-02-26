package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.dto.MemberCreateDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberMapper {
    @Select("SELECT * FROM members WHERE id = #{id}")
    Member selectMemberById(Long id);

    @Insert("INSERT INTO members (username, password, role, board_id) VALUES (#{username}, #{password}, #{role}, #{boardId})")
    void save(MemberCreateDto memberCreateDto);

    @Select("SELECT * FROM members   WHERE username = #{username}")
    Member findByUsername(String username);

    @Select("SELECT * FROM members WHERE role != 'ROLE_ADMIN'")
    List<Member> getAllMemberExceptAdmin();

}

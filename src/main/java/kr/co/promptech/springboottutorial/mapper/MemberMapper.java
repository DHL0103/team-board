package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Member;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MemberMapper {

    @Select("SELECT * FROM members WHERE id = #{id}")
    Member selectMemberById(Long id);

    @Select("SELECT * FROM members WHERE username = #{username}")
    Member findByUsername(String username);
}

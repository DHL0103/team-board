package kr.co.promptech.springboottutorial.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
interface MemberMapper {
    @Select("SELECT * FROM member WHERE id = #{id}")
    Member selectMemberById(Long id);
}

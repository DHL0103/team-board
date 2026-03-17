package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    Member selectMemberById(Long id);

    Member findByUsername(String username);

    boolean existsByUsername(String username);

    void insertMember(Member member);

    void updatePassword(@Param("id") Long id, @Param("password") String encodedPassword);
}

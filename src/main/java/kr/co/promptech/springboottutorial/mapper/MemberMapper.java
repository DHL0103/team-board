package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.model.dto.MemberSearchParam;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {

    Member selectMemberById(Long id);

    String findUsernameById(Long id);

    Member findByUsername(String username);

    boolean existsByUsername(String username);

    void insertMember(Member member);

    void updatePassword(@Param("id") Long id, @Param("password") String encodedPassword);

    void updateRole(@Param("id") Long id, @Param("role") MemberRole role);

    List<Member> getMembersPaged(@Param("search") MemberSearchParam search, @Param("size") int size);

    long countMembers(@Param("search") MemberSearchParam search);
}

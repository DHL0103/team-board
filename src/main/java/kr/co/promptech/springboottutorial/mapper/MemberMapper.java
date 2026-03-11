package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    Member selectMemberById(Long id);

    Member findByUsername(String username);

    boolean existsByUsername(String username);

    void insertMember(Member member);
}

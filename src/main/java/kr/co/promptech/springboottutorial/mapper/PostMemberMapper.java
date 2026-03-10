package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMemberMapper {

    List<PostMember> findByPostId(Long postId);

    int countByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    void save(@Param("postId") Long postId, @Param("memberId") Long memberId);

    void delete(@Param("postId") Long postId, @Param("memberId") Long memberId);
}

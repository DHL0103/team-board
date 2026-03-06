package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMemberMapper {

    @Select("SELECT * FROM post_members WHERE post_id = #{postId}")
    List<PostMember> findByPostId(Long postId);

    @Select("SELECT COUNT(*) FROM post_members WHERE post_id = #{postId} AND member_id = #{memberId}")
    int countByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    @Insert("INSERT INTO post_members (post_id, member_id) VALUES (#{postId}, #{memberId})")
    void save(@Param("postId") Long postId, @Param("memberId") Long memberId);

    @Delete("DELETE FROM post_members WHERE post_id = #{postId} AND member_id = #{memberId}")
    void delete(@Param("postId") Long postId, @Param("memberId") Long memberId);
}

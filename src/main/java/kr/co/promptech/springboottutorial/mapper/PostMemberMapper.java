package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostMember;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import kr.co.promptech.springboottutorial.model.dto.MyAssignedPostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMemberMapper {

    List<PostMember> findByPostId(Long postId);

    List<BoardMemberResponseDto> findAssigneesByPostId(Long postId);

    boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    void save(@Param("postId") Long postId, @Param("memberId") Long memberId);

    void delete(@Param("postId") Long postId, @Param("memberId") Long memberId);

    void deleteByPostId(Long postId);

    List<Long> findPostIdsByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    List<MyAssignedPostDto> findAssignedPostsByMemberId(@Param("memberId") Long memberId);
}

package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import kr.co.promptech.springboottutorial.model.dto.MyAssignedPostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface PostMemberMapper {

    List<BoardMemberResponseDto> findAssigneesByPostId(Long postId);

    boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    void save(@Param("postId") Long postId, @Param("memberId") Long memberId);

    void deleteByPostId(Long postId);

    Set<Long> findPostIdsByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    List<MyAssignedPostDto> findAssignedPostsByMemberId(@Param("memberId") Long memberId);
}

package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.BoardMember;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import kr.co.promptech.springboottutorial.model.dto.InvitedBoardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMemberMapper {

    BoardMember findByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    void save(@Param("boardId") Long boardId, @Param("memberId") Long memberId, @Param("boardRole") BoardRole boardRole);

    void updateRole(@Param("boardId") Long boardId, @Param("memberId") Long memberId, @Param("boardRole") BoardRole boardRole);

    void delete(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    List<BoardMemberResponseDto> findMembersByBoardId(Long boardId);

    List<BoardMemberResponseDto> findUsersByBoardId(@Param("boardId") Long boardId);

    List<InvitedBoardDto> findInvitedBoardsByMemberId(@Param("memberId") Long memberId);

    List<BoardMemberResponseDto> searchMembersWithBoardRole(@Param("boardId") Long boardId, @Param("username") String username);
}

package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.BoardMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMemberMapper {

    @Select("SELECT * FROM board_members WHERE board_id = #{boardId}")
    List<BoardMember> findByBoardId(Long boardId);

    @Select("SELECT * FROM board_members WHERE member_id = #{memberId}")
    List<BoardMember> findByMemberId(Long memberId);

    @Select("SELECT * FROM board_members WHERE board_id = #{boardId} AND member_id = #{memberId}")
    BoardMember findByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    @Insert("INSERT INTO board_members (board_id, member_id, board_role) VALUES (#{boardId}, #{memberId}, #{boardRole})")
    void save(@Param("boardId") Long boardId, @Param("memberId") Long memberId, @Param("boardRole") String boardRole);

    @Update("UPDATE board_members SET board_role = #{boardRole} WHERE board_id = #{boardId} AND member_id = #{memberId}")
    void updateRole(@Param("boardId") Long boardId, @Param("memberId") Long memberId, @Param("boardRole") String boardRole);

    @Delete("DELETE FROM board_members WHERE board_id = #{boardId} AND member_id = #{memberId}")
    void delete(@Param("boardId") Long boardId, @Param("memberId") Long memberId);
}

package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.BoardMember;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMemberMapper {

    @Select("SELECT * FROM board_members WHERE board_id = #{boardId} AND member_id = #{memberId}")
    BoardMember findByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    @Insert("INSERT INTO board_members (board_id, member_id, board_role) VALUES (#{boardId}, #{memberId}, #{boardRole})")
    void save(@Param("boardId") Long boardId, @Param("memberId") Long memberId, @Param("boardRole") String boardRole);

    @Update("UPDATE board_members SET board_role = #{boardRole} WHERE board_id = #{boardId} AND member_id = #{memberId}")
    void updateRole(@Param("boardId") Long boardId, @Param("memberId") Long memberId, @Param("boardRole") String boardRole);

    @Delete("DELETE FROM board_members WHERE board_id = #{boardId} AND member_id = #{memberId}")
    void delete(@Param("boardId") Long boardId, @Param("memberId") Long memberId);

    @ConstructorArgs({
            @Arg(column = "member_id",  javaType = Long.class),
            @Arg(column = "username",   javaType = String.class),
            @Arg(column = "board_role", javaType = String.class)
    })
    @Select("SELECT bm.member_id, m.username, bm.board_role " +
            "FROM board_members bm " +
            "JOIN members m ON bm.member_id = m.id " +
            "WHERE bm.board_id = #{boardId} " +
            "ORDER BY bm.board_role, m.username")
    List<BoardMemberResponseDto> findMembersByBoardId(Long boardId);
}

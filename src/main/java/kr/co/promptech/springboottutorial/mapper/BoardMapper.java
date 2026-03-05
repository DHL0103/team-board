package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("SELECT * FROM boards")
    List<Board> getAllBoards();

    @Select("SELECT * FROM boards WHERE id = #{id}")
    Board getBoardById(Long id);

    @Insert("INSERT INTO boards (name, color, description) VALUES (#{name}, #{color}, #{description})")
    void createBoard(BoardCreateDto boardCreateDto);

    @Update("UPDATE boards SET name = #{dto.name}, color = #{dto.color}, description = #{dto.description} WHERE id = #{id}")
    void updateBoard(@Param("id") Long id, @Param("dto") BoardCreateDto boardCreateDto);

    @Delete("DELETE FROM boards WHERE id = #{id}")
    void deleteBoard(Long id);

    @Select("<script>SELECT * FROM boards WHERE id IN <foreach collection='list' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Board> getBoardsByIds(List<Long> ids);

    @Select("SELECT b.* FROM boards b JOIN board_members bm ON b.id = bm.board_id WHERE bm.member_id = #{memberId} AND bm.board_role != 'REQUESTED'")
    List<Board> getBoardsByMemberId(Long memberId);
}

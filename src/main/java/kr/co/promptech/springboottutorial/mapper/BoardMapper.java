package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("SELECT * FROM boards")
    List<Board> getAllBoards();

    @Insert("INSERT INTO boards (name, slug, description) VALUES (#{name}, #{slug}, #{description})")
    void createBoard(BoardCreateDto boardCreateDto);

    @Update("UPDATE boards SET name = #{dto.name}, slug = #{dto.slug}, description = #{dto.description} WHERE id = #{id}")
    void updateBoard(@Param("id") Long id, @Param("dto") BoardCreateDto boardCreateDto);

    @Delete("DELETE FROM boards WHERE id = #{id}")
    void deleteBoard(Long id);
}

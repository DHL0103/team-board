package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("SELECT * FROM boards")
    List<Board> getAllBoards();

    @Insert("INSERT INTO boards (name, slug, description) VALUES (#{name}, #{slug}, #{description})")
    void createBoard(BoardCreateDto boardCreateDto);
}

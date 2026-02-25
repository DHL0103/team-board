package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("SELECT * FROM boards")
    List<Board> getAllBoards();
}

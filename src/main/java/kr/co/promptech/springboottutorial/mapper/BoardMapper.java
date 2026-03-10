package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<BoardResponseDto> getAllBoards();

    Board getBoardById(Long id);

    void createBoard(Board board);

    List<BoardResponseDto> getBoardsByMemberId(Long memberId);
}

package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import kr.co.promptech.springboottutorial.model.dto.BoardUpdateDto;

@Mapper
public interface BoardMapper {

    List<BoardResponseDto> getAllBoards();

    Board getBoardById(Long id);

    BoardResponseDto getBoardDtoById(Long id);

    void createBoard(Board board);

    List<BoardResponseDto> getBoardsByMemberId(Long memberId);

    void updateStatus(@Param("id") Long id, @Param("status") String status);

    void updateBoard(@Param("id") Long id, @Param("dto") BoardUpdateDto dto);
}

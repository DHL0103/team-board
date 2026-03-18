package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.model.dto.BoardSearchResultDto;
import kr.co.promptech.springboottutorial.model.dto.BoardUpdateDto;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<BoardResponseDto> getAllBoards();

    Board getBoardById(Long id);

    BoardResponseDto getBoardDtoById(Long id);

    void createBoard(Board board);

    List<BoardResponseDto> getBoardsByMemberId(Long memberId);

    void updateStatus(@Param("id") Long id, @Param("status") BoardStatus status);

    void updateBoard(@Param("id") Long id, @Param("dto") BoardUpdateDto dto);

    List<BoardSearchResultDto> searchBoards(@Param("keyword") String keyword, @Param("memberId") Long memberId);
}

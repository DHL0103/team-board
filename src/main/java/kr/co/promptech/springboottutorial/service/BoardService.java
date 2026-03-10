package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.BoardMapper;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    public List<BoardResponseDto> getAllBoardDtos() {
        return boardMapper.getAllBoards();
    }

    public Board getBoardById(Long id) {
        return boardMapper.getBoardById(id);
    }

    public BoardResponseDto getBoardDtoById(Long id) {
        return new BoardResponseDto(boardMapper.getBoardById(id));
    }

    public Long createBoard(BoardCreateDto boardCreateDto) {
        Board board = Board.builder()
                .name(boardCreateDto.getName())
                .color(boardCreateDto.getColor())
                .description(boardCreateDto.getDescription())
                .build();
        boardMapper.createBoard(board);
        return board.getId();
    }

    public void updateBoard(Long id, BoardCreateDto boardCreateDto) {
        boardMapper.updateBoard(id, boardCreateDto);
    }

    public void deleteBoard(Long id) {
        boardMapper.deleteBoard(id);
    }

    public List<BoardResponseDto> getBoardDtosByMemberId(Long memberId) {
        return boardMapper.getBoardsByMemberId(memberId);
    }
}

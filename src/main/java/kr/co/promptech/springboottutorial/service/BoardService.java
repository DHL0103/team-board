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

    public List<Board> getAllBoards() {
        return boardMapper.getAllBoards();
    }

    public List<BoardResponseDto> getAllBoardDtos() {
        return boardMapper.getAllBoards().stream()
                .map(BoardResponseDto::new)
                .toList();
    }

    public Board getBoardById(Long id) {
        return boardMapper.getBoardById(id);
    }

    public void createBoard(BoardCreateDto boardCreateDto){
        boardMapper.createBoard(boardCreateDto);
    }

    public void updateBoard(Long id, BoardCreateDto boardCreateDto){
        boardMapper.updateBoard(id, boardCreateDto);
    }

    public void deleteBoard(Long id) {
        boardMapper.deleteBoard(id);
    }

    public List<Board> getBoardsByMemberId(Long memberId) {
        return boardMapper.getBoardsByMemberId(memberId);
    }

    public List<BoardResponseDto> getBoardsByMemberIdDtos(Long memberId) {
        return boardMapper.getBoardsByMemberId(memberId).stream()
                .map(BoardResponseDto::new)
                .toList();
    }
}

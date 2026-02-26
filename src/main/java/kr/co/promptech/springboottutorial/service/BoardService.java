package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.BoardMapper;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
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

    public void createBoard(BoardCreateDto boardCreateDto){
        boardMapper.createBoard(boardCreateDto);
    }

    public void updateBoard(Long id, BoardCreateDto boardCreateDto){
        boardMapper.updateBoard(id, boardCreateDto);
    }
}

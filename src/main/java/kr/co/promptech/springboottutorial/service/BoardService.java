package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.BoardMapper;
import kr.co.promptech.springboottutorial.model.Board;
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
}

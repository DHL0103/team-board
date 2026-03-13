package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.BoardMapper;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import kr.co.promptech.springboottutorial.model.dto.BoardDetailDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final BoardMemberService boardMemberService;
    private final PostService postService;

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

public List<BoardResponseDto> getBoardDtosByMemberId(Long memberId) {
        return boardMapper.getBoardsByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public BoardDetailDto getBoardDetail(Long boardId, CustomUser user) {
        BoardResponseDto board = getBoardDtoById(boardId);
        boolean isManager = user.getRole() == MemberRole.ROLE_ADMIN
                || boardMemberService.isManager(boardId, user.getId());
        return BoardDetailDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .color(board.getColor())
                .status(board.getStatus())
                .memberCount(board.getMemberCount())
                .isManager(isManager)
                .postList(postService.getPostDtosByBoardId(boardId))
                .boardUserList(boardMemberService.getUsersByBoardId(boardId))
                .build();
    }

}

package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.exception.BoardNotFoundException;
import kr.co.promptech.springboottutorial.mapper.BoardMapper;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.dto.BoardCreateDto;
import kr.co.promptech.springboottutorial.model.dto.BoardDetailDto;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import kr.co.promptech.springboottutorial.model.dto.BoardSearchResultDto;
import kr.co.promptech.springboottutorial.model.dto.BoardUpdateDto;
import kr.co.promptech.springboottutorial.model.enums.BoardStatus;
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

    public List<BoardResponseDto> getAllBoardDtos() {
        return boardMapper.getAllBoards();
    }

    public Board getBoardById(Long id) {
        return boardMapper.getBoardById(id);
    }

    public BoardResponseDto getBoardDtoById(Long id) {
        return boardMapper.getBoardDtoById(id);
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

    public List<BoardResponseDto> getBoardDtosByStatus(String status, String keyword, CustomUser user) {
        if (user.getRole() == MemberRole.ROLE_ADMIN) {
            return boardMapper.getAllBoardsByStatus(status, keyword);
        }
        return boardMapper.getBoardsByMemberIdAndStatus(user.getId(), status, keyword);
    }

    public void updateStatus(Long boardId, BoardStatus status) {
        boardMapper.updateStatus(boardId, status);
    }

    public void updateBoard(Long boardId, BoardUpdateDto dto) {
        if (!"ACTIVE".equals(dto.getStatus()) && !"INACTIVE".equals(dto.getStatus())) {
            throw new IllegalArgumentException("유효하지 않은 상태값입니다: " + dto.getStatus());
        }
        boardMapper.updateBoard(boardId, dto);
    }

    public List<BoardSearchResultDto> searchBoards(String keyword, Long memberId) {
        return boardMapper.searchBoards(keyword, memberId);
    }

    @Transactional(readOnly = true)
    public BoardDetailDto getBoardDetail(Long boardId, CustomUser user) {
        BoardResponseDto board = getBoardDtoById(boardId);
        if (board == null) {
            throw new BoardNotFoundException(boardId);
        }
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
                .boardUserList(boardMemberService.getUsersByBoardId(boardId))
                .build();
    }
}

package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.BoardMemberMapper;
import kr.co.promptech.springboottutorial.model.BoardMember;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardMemberService {

    private final BoardMemberMapper boardMemberMapper;

    public List<BoardMember> getByBoardId(Long boardId) {
        return boardMemberMapper.findByBoardId(boardId);
    }

    public List<BoardMember> getByMemberId(Long memberId) {
        return boardMemberMapper.findByMemberId(memberId);
    }

    public BoardMember getByBoardIdAndMemberId(Long boardId, Long memberId) {
        return boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
    }

    public boolean isManager(Long boardId, Long memberId) {
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
        return bm != null && "MANAGER".equals(bm.getBoardRole());
    }

    public boolean isMember(Long boardId, Long memberId) {
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
        return bm != null && ("USER".equals(bm.getBoardRole()) || "MANAGER".equals(bm.getBoardRole()));
    }

    public boolean isRequested(Long boardId, Long memberId) {
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
        if (bm == null) {
            return false;
        }
        return "REQUESTED".equals(bm.getBoardRole());
    }

    public void save(Long boardId, Long memberId, String boardRole) {
        boardMemberMapper.save(boardId, memberId, boardRole);
    }

    public void updateRole(Long boardId, Long memberId, String boardRole) {
        boardMemberMapper.updateRole(boardId, memberId, boardRole);
    }

    public void delete(Long boardId, Long memberId) {
        boardMemberMapper.delete(boardId, memberId);
    }

    public long countByBoardId(Long boardId) {
        return boardMemberMapper.countByBoardId(boardId);
    }

    public List<BoardMemberResponseDto> getMembersByBoardId(Long boardId) {
        return boardMemberMapper.findMembersByBoardId(boardId);
    }
}

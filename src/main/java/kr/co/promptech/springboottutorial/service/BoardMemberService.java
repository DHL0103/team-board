package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.BoardMemberMapper;
import kr.co.promptech.springboottutorial.model.BoardMember;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardMemberService {

    private final BoardMemberMapper boardMemberMapper;

    public boolean isManager(Long boardId, Long memberId) {
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
        return bm != null && BoardRole.MANAGER.name().equals(bm.getBoardRole());
    }

    public boolean isMember(Long boardId, Long memberId) {
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
        return bm != null && (BoardRole.USER.name().equals(bm.getBoardRole()) || BoardRole.MANAGER.name().equals(bm.getBoardRole()));
    }

    public boolean isRequested(Long boardId, Long memberId) {
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
        if (bm == null) {
            return false;
        }
        return BoardRole.REQUESTED.name().equals(bm.getBoardRole());
    }

    public void save(Long boardId, Long memberId, BoardRole boardRole) {
        boardMemberMapper.save(boardId, memberId, boardRole);
    }

    public void updateRole(Long boardId, Long memberId, BoardRole boardRole) {
        boardMemberMapper.updateRole(boardId, memberId, boardRole);
    }

    public void delete(Long boardId, Long memberId) {
        boardMemberMapper.delete(boardId, memberId);
    }

    public List<BoardMemberResponseDto> getMembersByBoardId(Long boardId) {
        return boardMemberMapper.findMembersByBoardId(boardId);
    }
}

package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.BoardMemberMapper;
import kr.co.promptech.springboottutorial.model.BoardMember;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import kr.co.promptech.springboottutorial.model.dto.InvitedBoardDto;
import kr.co.promptech.springboottutorial.model.dto.MemberBoardDto;
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

    public boolean isInvited(Long boardId, Long memberId) {
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId);
        if (bm == null) {
            return false;
        }
        return BoardRole.INVITED.name().equals(bm.getBoardRole());
    }

    public void save(Long boardId, Long memberId, BoardRole boardRole) {
        if (boardMemberMapper.findByBoardIdAndMemberId(boardId, memberId) != null) {
            throw new IllegalStateException("이미 보드에 추가된 멤버입니다.");
        }
        boardMemberMapper.save(boardId, memberId, boardRole);
    }

    public void updateRole(Long boardId, Long memberId, BoardRole boardRole) {
        boardMemberMapper.updateRole(boardId, memberId, boardRole);
    }

    public void demoteMember(Long boardId, Long memberId) {
        validateNotLastManager(boardId, memberId, "마지막 매니저는 강등할 수 없습니다.");
        boardMemberMapper.updateRole(boardId, memberId, BoardRole.USER);
    }

    public void removeMember(Long boardId, Long memberId) {
        validateNotLastManager(boardId, memberId, "마지막 매니저는 내보낼 수 없습니다.");
        boardMemberMapper.delete(boardId, memberId);
    }

    public void leaveBoard(Long boardId, Long memberId) {
        validateNotLastManager(boardId, memberId, "마지막 매니저는 보드를 나갈 수 없습니다.");
        boardMemberMapper.delete(boardId, memberId);
    }

    private void validateNotLastManager(Long boardId, Long memberId, String message) {
        if (isManager(boardId, memberId) && boardMemberMapper.countManagersByBoardId(boardId) <= 1) {
            throw new IllegalStateException(message);
        }
    }

    public List<BoardMemberResponseDto> getMembersByBoardId(Long boardId) {
        return boardMemberMapper.findMembersByBoardId(boardId);
    }

    public List<BoardMemberResponseDto> getUsersByBoardId(Long boardId) {
        return boardMemberMapper.findUsersByBoardId(boardId);
    }

    public List<InvitedBoardDto> getInvitedBoards(Long memberId) {
        return boardMemberMapper.findInvitedBoardsByMemberId(memberId);
    }

    public List<BoardMemberResponseDto> searchMembersForInvite(Long boardId, String username) {
        return boardMemberMapper.searchMembersWithBoardRole(boardId, username);
    }

    public List<MemberBoardDto> getBoardsByMemberId(Long memberId) {
        return boardMemberMapper.findBoardsByMemberId(memberId);
    }
}

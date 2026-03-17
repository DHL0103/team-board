package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.CommentMapper;
import kr.co.promptech.springboottutorial.model.Comment;
import kr.co.promptech.springboottutorial.model.dto.CommentCreateDto;
import kr.co.promptech.springboottutorial.model.dto.CommentResponseDto;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final BoardMemberService boardMemberService;

    public void save(Long memberId, CommentCreateDto dto) {
        Comment comment = Comment.builder()
                .postId(dto.getPostId())
                .memberId(memberId)
                .content(dto.getContent())
                .parentId(dto.getParentId())
                .groupId(dto.getGroupId())
                .depth(dto.getDepth())
                .build();
        commentMapper.save(comment);
    }

    public List<CommentResponseDto> findByPostId(Long postId, Long boardId) {
        return commentMapper.findByPostId(postId, boardId);
    }

    public void update(Long commentId, Long memberId, String content) {
        commentMapper.update(commentId, memberId, content);
    }

    public void delete(Long commentId, Long requesterId, MemberRole role, Long boardId) {
        boolean isManagerOrAdmin = role == MemberRole.ROLE_ADMIN
                || boardMemberService.isManager(boardId, requesterId);
        if (!isManagerOrAdmin) {
            Comment comment = commentMapper.findById(commentId);
            if (comment == null || !comment.getMemberId().equals(requesterId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        commentMapper.softDelete(commentId);
    }
}
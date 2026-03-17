package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.CommentMapper;
import kr.co.promptech.springboottutorial.model.Comment;
import kr.co.promptech.springboottutorial.model.dto.CommentCreateDto;
import kr.co.promptech.springboottutorial.model.dto.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

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
}
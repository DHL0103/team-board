package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.CommentMapper;
import kr.co.promptech.springboottutorial.model.Comment;
import kr.co.promptech.springboottutorial.model.dto.CommentCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
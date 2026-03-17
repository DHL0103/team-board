package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Comment;
import kr.co.promptech.springboottutorial.model.dto.CommentResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    void save(Comment comment);

    List<CommentResponseDto> findByPostId(@Param("postId") Long postId, @Param("boardId") Long boardId);

    void update(@Param("id") Long id, @Param("memberId") Long memberId, @Param("content") String content);
}
package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

    void save(Comment comment);
}
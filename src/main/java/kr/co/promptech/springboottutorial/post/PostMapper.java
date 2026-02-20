package kr.co.promptech.springboottutorial.post;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List; // 표준 List 임포트

@Mapper
public interface PostMapper { // 인터페이스(interface)여야 합니다!

    @Select("SELECT * FROM post")
    List<Post> getAllPost();
}

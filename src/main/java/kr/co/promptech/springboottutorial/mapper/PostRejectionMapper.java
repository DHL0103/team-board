package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostRejection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostRejectionMapper {

    @Insert("INSERT INTO post_rejections (post_id, reason) VALUES (#{postId}, #{reason})")
    void save(PostRejection postRejection);

    @Select("SELECT * FROM post_rejections WHERE post_id = #{postId} ORDER BY created_at DESC")
    List<PostRejection> findAllByPostId(Long postId);
}
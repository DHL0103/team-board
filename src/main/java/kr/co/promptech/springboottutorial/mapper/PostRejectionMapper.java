package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostRejection;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostRejectionMapper {

    @Insert("INSERT INTO post_rejections (post_id, reason, rejected_by, created_at) VALUES (#{postId}, #{reason}, #{rejectedBy}, #{createdAt})")
    void save(@Param("postId") Long postId, @Param("reason") String reason,
              @Param("rejectedBy") Long rejectedBy, @Param("createdAt") LocalDateTime createdAt);

    @Select("SELECT * FROM post_rejections WHERE post_id = #{postId} ORDER BY created_at DESC")
    List<PostRejection> findAllByPostId(Long postId);
}
package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostRejection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostRejectionMapper {

    void save(@Param("postId") Long postId, @Param("reason") String reason,
              @Param("rejectedBy") Long rejectedBy, @Param("createdAt") LocalDateTime createdAt);

    List<PostRejection> findAllByPostId(Long postId);
}

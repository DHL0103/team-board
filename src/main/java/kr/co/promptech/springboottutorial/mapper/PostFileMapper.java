package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostFile;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostFileMapper {
    @Insert("INSERT INTO post_files (post_id, original_name, stored_path, file_size, created_at) " +
            "VALUES (#{postId}, #{originalName}, #{storedPath}, #{fileSize}, #{createdAt})")
    void saveFile(@Param("postId") Long postId, @Param("originalName") String originalName,
                  @Param("storedPath") String storedPath, @Param("fileSize") Long fileSize,
                  @Param("createdAt") LocalDateTime createdAt);

    @Select("SELECT * FROM post_files WHERE post_id = #{postId}")
    List<PostFile> findByPostId(Long postId);

    @Select("SELECT * FROM post_files WHERE id = #{id}")
    PostFile findById(Long id);

    @Delete("DELETE FROM post_files WHERE id = #{id}")
    void deleteById(Long id);
}

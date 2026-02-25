package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface PostFileMapper {
    @Insert("INSERT INTO post_files (post_id, original_name, stored_path, file_size, created_at) " +
            "VALUES (#{postId}, #{originalName}, #{storedPath}, #{fileSize}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveFile(PostFile postFile);
}

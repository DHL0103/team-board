package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.PostFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostFileMapper {

    void saveFile(PostFile postFile);

    List<PostFile> findByPostId(Long postId);

    PostFile findById(Long id);

    void deleteById(Long id);

    void linkInlineImages(@Param("storedPaths") List<String> storedPaths, @Param("postId") Long postId);

    List<PostFile> findByStoredPaths(@Param("storedPaths") List<String> storedPaths);

    void deleteByIds(@Param("ids") List<Long> ids);
}

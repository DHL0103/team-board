package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper {

    Post getPostById(Long id);

    void createPost(Post post);

    void deletePost(@Param("id") Long id);

    void updatePost(@Param("id") Long id, @Param("title") String title,
                    @Param("content") String content, @Param("dueDate") LocalDateTime dueDate);

    List<Post> getRequestedPostsByBoardId(Long boardId);

    List<Post> getPostsByBoardId(Long boardId);

    List<Post> getPostsByBoardIdInProgress(@Param("boardId") Long boardId);

    List<Post> getPostsByBoardIdAndStatus(@Param("boardId") Long boardId, @Param("status") String status);

    List<Post> getPostsPagedByBoardId(@Param("boardId") Long boardId, @Param("status") String status,
                                      @Param("offset") int offset, @Param("size") int size);

    long countPostsByBoardId(@Param("boardId") Long boardId, @Param("status") String status);

    void updateStatus(Long id, PostStatus status);
}

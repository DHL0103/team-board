package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.PostFile;
import org.apache.ibatis.annotations.*;

import java.util.List; // 표준 List 임포트

@Mapper
public interface PostMapper { // 인터페이스(interface)여야 합니다!

    @Select("SELECT * FROM posts")
    List<Post> getAllPost();

    @Select("SELECT * FROM posts WHERE id = #{id}")
    Post getPostById(Long id);

    @Insert("""
        INSERT INTO posts (
            board_id,
            member_id,
            title,
            content,
            status,
            due_date,
            created_at,
            updated_at
        ) VALUES (
            #{boardId},
            #{memberId},
            #{title},
            #{content},
            #{status},
            #{dueDate},
            #{createdAt},
            #{updatedAt}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createPost(Post post);

    @Delete("DELETE FROM posts WHERE id = #{id}")
    void deletePost(Post post);

    @Update("""
        UPDATE posts
        SET
            title = #{title},
            content = #{content},
            due_date = #{dueDate},
            updated_at = #{updatedAt}
        WHERE id = #{id}
    """)
    void updatePost(Post post);

    @Select("SELECT * FROM posts WHERE status = 'REQUESTED'")
    List<Post> getRequestedPost();

    @Select("SELECT * FROM posts WHERE board_id = #{boardId}")
    List<Post> getPostsByBoardId(Long boardId);
}

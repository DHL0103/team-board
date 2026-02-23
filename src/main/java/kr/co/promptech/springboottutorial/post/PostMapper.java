package kr.co.promptech.springboottutorial.post;

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
}

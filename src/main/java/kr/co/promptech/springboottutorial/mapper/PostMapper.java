package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import org.apache.ibatis.annotations.*;

import java.util.List; // 표준 List 임포트

@Mapper
public interface PostMapper { // 인터페이스(interface)여야 합니다!

    @Select("SELECT * FROM posts")
    List<Post> getAllPost();

    @Select("SELECT * FROM posts WHERE status != 'APPROVED'")
    List<Post>getCurrentPost();

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
            title = #{dto.title},
            content = #{dto.content},
            due_date = #{dto.dueDate},
            updated_at = NOW()
        WHERE id = #{id}
    """)
    void updatePost(@Param("id") Long id, @Param("dto") PostCreateDto dto);

    @Select("SELECT * FROM posts WHERE status = 'REQUESTED'")
    List<Post> getRequestedPost();

    @Select("SELECT * FROM posts WHERE board_id = #{boardId}")
    List<Post> getPostsByBoardId(Long boardId);

    @Select("SELECT * FROM posts WHERE member_id = #{memberId}")
    List<Post> getPostsByMemberId(Long memberId);

    @Update("UPDATE posts SET status = #{status} WHERE id = #{id}")
    void updateStatus(Long id, String status);


}

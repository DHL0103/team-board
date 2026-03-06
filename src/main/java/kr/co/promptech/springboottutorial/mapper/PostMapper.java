package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Post;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

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
            title = #{title},
            content = #{content},
            due_date = #{dueDate},
            updated_at = NOW()
        WHERE id = #{id}
    """)
    void updatePost(@Param("id") Long id, @Param("title") String title,
                    @Param("content") String content, @Param("dueDate") LocalDateTime dueDate);

    @Select("SELECT * FROM posts WHERE status = 'REQUESTED'")
    List<Post> getRequestedPost();

    @Select("SELECT * FROM posts WHERE board_id = #{boardId}")
    List<Post> getPostsByBoardId(Long boardId);

    @Select("SELECT * FROM posts WHERE board_id = #{boardId} AND status IN ('PROGRESS', 'REJECTED')")
    List<Post> getPostsByBoardIdInProgress(@Param("boardId") Long boardId);

    @Select("SELECT * FROM posts WHERE board_id = #{boardId} AND status = #{status}")
    List<Post> getPostsByBoardIdAndStatus(@Param("boardId") Long boardId, @Param("status") String status);

    @Select("SELECT * FROM posts WHERE member_id = #{memberId}")
    List<Post> getPostsByMemberId(Long memberId);

    @Update("UPDATE posts SET status = #{status} WHERE id = #{id}")
    void updateStatus(Long id, String status);


}

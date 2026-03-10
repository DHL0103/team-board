package kr.co.promptech.springboottutorial.mapper;

import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.dto.BoardResponseDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @ConstructorArgs({
            @Arg(column = "id",           javaType = Long.class),
            @Arg(column = "name",         javaType = String.class),
            @Arg(column = "description",  javaType = String.class),
            @Arg(column = "color",        javaType = String.class),
            @Arg(column = "status",       javaType = String.class),
            @Arg(column = "member_count", javaType = long.class)
    })
    @Select("SELECT b.id, b.name, b.description, b.color, b.status, " +
            "COUNT(bm.member_id) AS member_count " +
            "FROM boards b " +
            "LEFT JOIN board_members bm ON b.id = bm.board_id AND bm.board_role != 'REQUESTED' " +
            "GROUP BY b.id")
    List<BoardResponseDto> getAllBoards();

    @Select("SELECT * FROM boards WHERE id = #{id}")
    Board getBoardById(Long id);

    @Insert("INSERT INTO boards (name, color, description) VALUES (#{name}, #{color}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createBoard(Board board);

@ConstructorArgs({
            @Arg(column = "id",           javaType = Long.class),
            @Arg(column = "name",         javaType = String.class),
            @Arg(column = "description",  javaType = String.class),
            @Arg(column = "color",        javaType = String.class),
            @Arg(column = "status",       javaType = String.class),
            @Arg(column = "member_count", javaType = long.class)
    })
    @Select("SELECT b.id, b.name, b.description, b.color, b.status, " +
            "COUNT(bm2.member_id) AS member_count " +
            "FROM boards b " +
            "JOIN board_members bm ON b.id = bm.board_id AND bm.member_id = #{memberId} AND bm.board_role != 'REQUESTED' " +
            "LEFT JOIN board_members bm2 ON b.id = bm2.board_id AND bm2.board_role != 'REQUESTED' " +
            "GROUP BY b.id")
    List<BoardResponseDto> getBoardsByMemberId(Long memberId);
}

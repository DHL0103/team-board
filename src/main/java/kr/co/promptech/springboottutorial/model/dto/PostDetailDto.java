package kr.co.promptech.springboottutorial.model.dto;

import kr.co.promptech.springboottutorial.model.PostFile;
import kr.co.promptech.springboottutorial.model.PostRejection;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailDto {
    private Long id;
    private Long boardId;
    private Long memberId;
    private String title;
    private String content;
    private PostStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PostFile> postFiles;
    private List<PostRejection> rejections;
    private String boardName;
    private String boardColor;
    private boolean canModify;
    private List<BoardMemberResponseDto> boardUserList;
    private List<BoardMemberResponseDto> postAssignees;
}

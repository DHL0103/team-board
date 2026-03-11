package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.exception.PostNotFoundException;
import kr.co.promptech.springboottutorial.model.BoardMember;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.mapper.BoardMemberMapper;
import kr.co.promptech.springboottutorial.mapper.PostMapper;
import kr.co.promptech.springboottutorial.mapper.PostMemberMapper;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import kr.co.promptech.springboottutorial.model.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostFileService postFileService;
    private final PostMemberMapper postMemberMapper;
    private final BoardMemberMapper boardMemberMapper;
    private final BoardMemberService boardMemberService;

    public Post getPostById(Long id) {
        Post post = postMapper.getPostById(id);
        if (post == null) {
            throw new PostNotFoundException(id);
        }
        return post;
    }

    public List<PostResponseDto> getRequestedPostDtosByBoardId(Long boardId) {
        return postMapper.getRequestedPostsByBoardId(boardId).stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public Long createPost(PostCreateDto postCreateDto, Long memberId) {
        String dueDateStr = postCreateDto.getDueDate();
        LocalDateTime dueDate = null;
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            dueDate = LocalDateTime.parse(dueDateStr);
        }

        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .dueDate(dueDate)
                .memberId(memberId)
                .boardId(postCreateDto.getBoardId())
                .status(PostStatus.PROGRESS.name())
                .createdAt(LocalDateTime.now())
                .build();

        postMapper.createPost(post);

        // board USER가 생성한 경우 본인을 자동으로 담당자에 추가
        BoardMember bm = boardMemberMapper.findByBoardIdAndMemberId(post.getBoardId(), memberId);
        if (bm != null && BoardRole.USER.name().equals(bm.getBoardRole())) {
            postMemberMapper.save(post.getId(), memberId);
        }

        // 명시적으로 지정된 담당자 저장 (중복은 UNIQUE KEY가 처리)
        List<Long> assigneeIds = postCreateDto.getAssigneeIds();
        if (assigneeIds != null) {
            for (Long assigneeId : assigneeIds) {
                postMemberMapper.save(post.getId(), assigneeId);
            }
        }

        return post.getId();
    }

    public void deletePost(Post post) {
        postFileService.deleteFilesByPostId(post.getId());
        postMapper.deletePost(post);
    }

    public List<PostResponseDto> getPostDtosByBoardId(Long boardId) {
        return postMapper.getPostsByBoardId(boardId).stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public List<PostResponseDto> getPostDtosByBoardIdAndStatus(Long boardId, String status) {
        List<Post> posts = PostStatus.PROGRESS.name().equals(status)
                ? postMapper.getPostsByBoardIdInProgress(boardId)
                : postMapper.getPostsByBoardIdAndStatus(boardId, status);
        return posts.stream().map(PostResponseDto::new).toList();
    }

    public void updatePost(Long id, PostCreateDto postCreateDto) {
        String dueDateStr = postCreateDto.getDueDate();
        LocalDateTime dueDate = null;
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            dueDate = LocalDateTime.parse(dueDateStr);
        }
        postMapper.updatePost(id, postCreateDto.getTitle(), postCreateDto.getContent(), dueDate);

        postMemberMapper.deleteByPostId(id);
        List<Long> assigneeIds = postCreateDto.getAssigneeIds();
        if (assigneeIds != null) {
            for (Long assigneeId : assigneeIds) {
                postMemberMapper.save(id, assigneeId);
            }
        }
    }

    public List<BoardMemberResponseDto> getAssigneesByPostId(Long postId) {
        return postMemberMapper.findAssigneesByPostId(postId);
    }

    public boolean isAssignee(Long postId, Long memberId) {
        return postMemberMapper.countByPostIdAndMemberId(postId, memberId) > 0;
    }

    public boolean canModify(Long postId, Long boardId, Long memberId, MemberRole role) {
        boolean isAdminOrManager = role == MemberRole.ROLE_ADMIN
                || boardMemberService.isManager(boardId, memberId);
        if (isAdminOrManager) {
            return true;
        }
        Post post = getPostById(postId);
        if (PostStatus.APPROVED.name().equals(post.getStatus())
                || PostStatus.REQUESTED.name().equals(post.getStatus())) {
            return false;
        }
        return isAssignee(postId, memberId);
    }

    public void updateStatus(Long id, PostStatus status) {
        postMapper.updateStatus(id, status);
    }
}

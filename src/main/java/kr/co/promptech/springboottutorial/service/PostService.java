package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.exception.PostNotFoundException;
import kr.co.promptech.springboottutorial.util.HtmlSanitizer;
import kr.co.promptech.springboottutorial.model.Board;
import kr.co.promptech.springboottutorial.model.BoardMember;
import kr.co.promptech.springboottutorial.model.CustomUser;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.mapper.BoardMapper;
import kr.co.promptech.springboottutorial.mapper.BoardMemberMapper;
import kr.co.promptech.springboottutorial.mapper.MemberMapper;
import kr.co.promptech.springboottutorial.mapper.PostMapper;
import kr.co.promptech.springboottutorial.mapper.PostMemberMapper;
import kr.co.promptech.springboottutorial.mapper.PostRejectionMapper;
import kr.co.promptech.springboottutorial.model.enums.BoardRole;
import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import kr.co.promptech.springboottutorial.model.enums.PostStatus;
import kr.co.promptech.springboottutorial.model.dto.BoardMemberResponseDto;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import kr.co.promptech.springboottutorial.model.dto.PostDetailDto;
import kr.co.promptech.springboottutorial.model.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final BoardMapper boardMapper;
    private final PostRejectionService postRejectionService;
    private final CommentService commentService;
    private final HtmlSanitizer htmlSanitizer;
    private final MemberMapper memberMapper;

    public Post getPostById(Long id) {
        Post post = postMapper.getPostById(id);
        if (post == null) {
            throw new PostNotFoundException(id);
        }
        return post;
    }

    public PostResponseDto getPostDtoById(Long id) {
        return new PostResponseDto(getPostById(id));
    }


    public List<PostResponseDto> getRequestedPostDtosByBoardId(Long boardId) {
        return postMapper.getRequestedPostsByBoardId(boardId).stream()
                .map(PostResponseDto::new)
                .toList();
    }

    @Transactional
    public void createPost(PostCreateDto postCreateDto, Long memberId, List<MultipartFile> files) {
        String dueDateStr = postCreateDto.getDueDate();
        LocalDateTime dueDate = null;
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            dueDate = LocalDateTime.parse(dueDateStr);
        }

        String sanitizedContent = htmlSanitizer.sanitize(postCreateDto.getContent());

        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(sanitizedContent)
                .dueDate(dueDate)
                .memberId(memberId)
                .boardId(postCreateDto.getBoardId())
                .status(PostStatus.PROGRESS.name())
                .createdAt(LocalDateTime.now())
                .build();

        postMapper.createPost(post);

        postFileService.linkInlineImages(sanitizedContent, post.getId());

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

        postFileService.saveFiles(files, post.getId());
    }

    @Transactional
    public void deletePost(Long id) {
        postFileService.deleteFilesByPostId(id);
        postMapper.deletePost(id);
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

    @Transactional
    public void updatePost(Long id, PostCreateDto postCreateDto, List<MultipartFile> files, List<Long> deleteFileIds) {
        String dueDateStr = postCreateDto.getDueDate();
        LocalDateTime dueDate = null;
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            dueDate = LocalDateTime.parse(dueDateStr);
        }

        String sanitizedContent = htmlSanitizer.sanitize(postCreateDto.getContent());
        String oldContent = getPostById(id).getContent();
        postFileService.syncInlineImages(oldContent, sanitizedContent, id);

        postMapper.updatePost(id, postCreateDto.getTitle(), sanitizedContent, dueDate);

        postMemberMapper.deleteByPostId(id);
        List<Long> assigneeIds = postCreateDto.getAssigneeIds();
        if (assigneeIds != null) {
            for (Long assigneeId : assigneeIds) {
                postMemberMapper.save(id, assigneeId);
            }
        }

        postFileService.deleteFiles(deleteFileIds);
        postFileService.saveFiles(files, id);
    }

    public List<BoardMemberResponseDto> getAssigneesByPostId(Long postId) {
        return postMemberMapper.findAssigneesByPostId(postId);
    }

    public boolean isAssignee(Long postId, Long memberId) {
        return postMemberMapper.existsByPostIdAndMemberId(postId, memberId);
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

    @Transactional
    public void updateStatus(Long id, PostStatus status) {
        postMapper.updateStatus(id, status);
    }

    @Transactional
    public void rejectPost(Long postId, String reason, Long rejectedBy) {
        postMapper.updateStatus(postId, PostStatus.REJECTED);
        postRejectionService.save(postId, reason, rejectedBy);
    }



    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long postId, CustomUser user) {
        Post post = getPostById(postId);
        Board board = boardMapper.getBoardById(post.getBoardId());
        boolean canModify = user != null && canModify(postId, post.getBoardId(), user.getId(), user.getRole());
        boolean isManagerOrAdmin = user != null && (user.getRole() == MemberRole.ROLE_ADMIN
                || boardMemberService.isManager(post.getBoardId(), user.getId()));

        return PostDetailDto.builder()
                .id(post.getId())
                .boardId(post.getBoardId())
                .memberId(post.getMemberId())
                .writerName(memberMapper.findUsernameById(post.getMemberId()))
                .title(post.getTitle())
                .content(post.getContent())
                .status(post.getStatus())
                .dueDate(post.getDueDate())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .postFiles(postFileService.getFilesByPostId(postId))
                .rejections(postRejectionService.getDtosByPostId(postId))
                .boardName(board != null ? board.getName() : null)
                .boardColor(board != null ? board.getColor() : null)
                .canModify(canModify)
                .boardUserList(boardMemberService.getUsersByBoardId(post.getBoardId()))
                .postAssignees(postMemberMapper.findAssigneesByPostId(postId))
                .commentList(commentService.findByPostId(postId, post.getBoardId()))
                .currentMemberId(user != null ? user.getId() : null)
                .currentMemberName(user != null ? user.getUsername() : null)
                .currentUserIsManagerOrAdmin(isManagerOrAdmin)
                .build();
    }
}

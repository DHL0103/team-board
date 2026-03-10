package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.exception.PostNotFoundException;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.mapper.PostMapper;
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
                .status("PROGRESS")
                .createdAt(LocalDateTime.now())
                .build();

        postMapper.createPost(post);
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
        List<Post> posts = status.equals("PROGRESS")
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
    }

    public void updateStatus(Long id, String status) {
        postMapper.updateStatus(id, status);
    }
}

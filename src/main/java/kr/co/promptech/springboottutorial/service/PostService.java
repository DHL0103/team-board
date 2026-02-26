package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.exception.PostNotFoundException;
import kr.co.promptech.springboottutorial.service.PostFileService;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.mapper.MemberMapper;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.mapper.PostMapper;
import kr.co.promptech.springboottutorial.model.PostFile;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final MemberMapper memberMapper;
    private final PostFileService postFileService;


    public List<Post> getAllPost(){
        return postMapper.getAllPost();
    }

    public List<Post> getCurrentPost(){
        return postMapper.getCurrentPost();
    }

    public Post getPostById(Long id){
        Post post = postMapper.getPostById(id);
        if (post == null) throw new PostNotFoundException(id);
        return post;
    }

    public List<Post> getRequestedPost(){
        return postMapper.getRequestedPost();
    }

    public Long createPost(PostCreateDto postCreateDto, String username){
        Member member = memberMapper.findByUsername(username);

        Post post = new Post();
        post.setTitle(postCreateDto.getTitle());
        post.setContent(postCreateDto.getContent());
        post.setDueDate(postCreateDto.getDueDate());
        post.setMemberId(member.getId()); // 작성자 ID 주입
        post.setBoardId(postCreateDto.getBoardId());         // 게시판 ID 주입
        post.setStatus("PROGRESS");       // 기본 상태는 진행중으로.
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(null);

        postMapper.createPost(post);
        return post.getId();
    }

    public void deletePost(Post post){
        postFileService.deleteFilesByPostId(post.getId());
        postMapper.deletePost(post);
    }

    public List<Post> getPostsByBoardId(Long boardId) {
        return postMapper.getPostsByBoardId(boardId);
    }

    public void updatePost(Post post, PostCreateDto postCreateDto){

        post.setTitle(postCreateDto.getTitle());
        post.setContent(postCreateDto.getContent());
        post.setDueDate(postCreateDto.getDueDate());
        post.setUpdatedAt(LocalDateTime.now());

        postMapper.updatePost(post);
    }

    public void updateStatus(Long id, String status){
        postMapper.updateStatus(id,status);
    }
}

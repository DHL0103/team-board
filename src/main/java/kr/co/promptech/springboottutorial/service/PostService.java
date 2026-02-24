package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.mapper.MemberMapper;
import kr.co.promptech.springboottutorial.model.Post;
import kr.co.promptech.springboottutorial.mapper.PostMapper;
import kr.co.promptech.springboottutorial.model.dto.PostCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final MemberMapper memberMapper;

    public List<Post> getAllPost(){
        return postMapper.getAllPost();
    }

    public Post getPostById(Long id){
        return postMapper.getPostById(id);
    }

    public void createPost(PostCreateDto postCreateDto, String username){
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
    }

    public void deletePost(Post post){
        postMapper.deletePost(post);
    }

    public void updatePost(Post post, PostCreateDto postCreateDto){

        post.setTitle(postCreateDto.getTitle());
        post.setContent(postCreateDto.getContent());
        post.setDueDate(postCreateDto.getDueDate());
        post.setUpdatedAt(LocalDateTime.now());

        postMapper.updatePost(post);
    }



}

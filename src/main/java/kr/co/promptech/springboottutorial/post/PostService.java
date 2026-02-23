package kr.co.promptech.springboottutorial.post;

import kr.co.promptech.springboottutorial.member.Member;
import kr.co.promptech.springboottutorial.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.jdbc.Null;
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

    public void createPost(Long boardId,String title, String content, LocalDateTime dueDate, String username){
        Member member = memberMapper.findByUsername(username);

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setDueDate(dueDate);
        post.setMemberId(member.getId()); // 작성자 ID 주입
        post.setBoardId(boardId);         // 게시판 ID 주입
        post.setStatus("PROGRESS");       // 기본 상태는 진행중으로.
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(null);

        postMapper.createPost(post);
    }

}

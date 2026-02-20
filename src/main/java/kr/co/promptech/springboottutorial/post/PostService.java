package kr.co.promptech.springboottutorial.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;

    public List<Post> getAllPost(){
        return postMapper.getAllPost();
    }

    public Post getPostById(Long id){
        return postMapper.getPostById(id);
    }

}

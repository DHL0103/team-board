package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostRejectionMapper;
import kr.co.promptech.springboottutorial.model.PostRejection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostRejectionService {

    private final PostRejectionMapper postRejectionMapper;

    public void save(Long postId, String reason) {
        PostRejection rejection = new PostRejection();
        rejection.setPostId(postId);
        rejection.setReason(reason);
        rejection.setCreatedAt(LocalDateTime.now());
        postRejectionMapper.save(rejection);
    }

    public List<PostRejection> getByPostId(Long postId) {
        return postRejectionMapper.findAllByPostId(postId);
    }
}
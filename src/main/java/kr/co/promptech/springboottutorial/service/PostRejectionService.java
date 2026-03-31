package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostRejectionMapper;
import kr.co.promptech.springboottutorial.model.dto.PostRejectionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostRejectionService {

    private final PostRejectionMapper postRejectionMapper;

    public void save(Long postId, String reason, Long rejectedBy) {
        postRejectionMapper.save(postId, reason, rejectedBy, LocalDateTime.now());
    }

    public List<PostRejectionDto> getDtosByPostId(Long postId) {
        return postRejectionMapper.findAllByPostIdWithNames(postId);
    }
}
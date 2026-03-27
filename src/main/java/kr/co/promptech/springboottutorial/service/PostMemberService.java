package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostMemberMapper;
import kr.co.promptech.springboottutorial.model.dto.MyAssignedPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostMemberService {

    private final PostMemberMapper postMemberMapper;

    public boolean isAssignee(Long postId, Long memberId) {
        return postMemberMapper.existsByPostIdAndMemberId(postId, memberId);
    }

    public void save(Long postId, Long memberId) {
        postMemberMapper.save(postId, memberId);
    }

    public void delete(Long postId, Long memberId) {
        postMemberMapper.delete(postId, memberId);
    }

    public Set<Long> getMyPostIds(Long boardId, Long memberId) {
        return new HashSet<>(postMemberMapper.findPostIdsByBoardIdAndMemberId(boardId, memberId));
    }

    public List<MyAssignedPostDto> getAssignedPosts(Long memberId) {
        return postMemberMapper.findAssignedPostsByMemberId(memberId);
    }
}

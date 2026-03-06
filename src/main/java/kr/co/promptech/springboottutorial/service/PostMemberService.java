package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostMemberMapper;
import kr.co.promptech.springboottutorial.model.PostMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostMemberService {

    private final PostMemberMapper postMemberMapper;

    public List<PostMember> getByPostId(Long postId) {
        return postMemberMapper.findByPostId(postId);
    }

    public boolean isAssignee(Long postId, Long memberId) {
        return postMemberMapper.countByPostIdAndMemberId(postId, memberId) > 0;
    }

    public void save(Long postId, Long memberId) {
        postMemberMapper.save(postId, memberId);
    }

    public void delete(Long postId, Long memberId) {
        postMemberMapper.delete(postId, memberId);
    }
}

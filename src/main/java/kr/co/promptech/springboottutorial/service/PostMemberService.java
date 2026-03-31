package kr.co.promptech.springboottutorial.service;

import kr.co.promptech.springboottutorial.mapper.PostMemberMapper;
import kr.co.promptech.springboottutorial.model.dto.MyAssignedPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostMemberService {

    private final PostMemberMapper postMemberMapper;

    public Set<Long> getMyPostIds(Long boardId, Long memberId) {
        return postMemberMapper.findPostIdsByBoardIdAndMemberId(boardId, memberId);
    }

    public List<MyAssignedPostDto> getAssignedPosts(Long memberId) {
        return postMemberMapper.findAssignedPostsByMemberId(memberId);
    }
}

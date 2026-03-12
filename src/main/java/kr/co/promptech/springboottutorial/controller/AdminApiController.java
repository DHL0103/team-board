package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.MemberBoardDto;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api")
public class AdminApiController {

    private final BoardMemberService boardMemberService;

    /**
     * @param memberId 조회할 멤버 ID
     * @return 해당 멤버가 속한 보드 목록 (MemberBoardDto JSON)
     * 멤버 상세 모달에서 소속 보드 및 역할을 lazy 로딩으로 조회
     */
    @GetMapping("/members/{memberId}/boards")
    public List<MemberBoardDto> getMemberBoards(@PathVariable Long memberId) {
        return boardMemberService.getBoardsByMemberId(memberId);
    }
}

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

    @GetMapping("/members/{memberId}/boards")
    public List<MemberBoardDto> getMemberBoards(@PathVariable Long memberId) {
        return boardMemberService.getBoardsByMemberId(memberId);
    }
}

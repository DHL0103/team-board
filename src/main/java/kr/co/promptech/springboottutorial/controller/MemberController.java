package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.MemberCreateDto;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.service.BoardMemberService;
import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final BoardMemberService boardMemberService;

    /**
     * @param id 조회할 멤버의 PK
     * @return 해당 멤버 객체 (JSON)
     * id로 특정 멤버 정보를 조회하는 REST API
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Member getMemberDetailPage(@PathVariable Long id){
        return memberService.getMemberById(id);
    }

    /**
     * @return 로그인 폼 뷰 이름 (login_form)
     * 로그인 페이지 렌더링, 실제 인증 처리는 Spring Security가 담당
     */
    @GetMapping("/login")
    public String login_page() {
        return "login_form";
    }

    /**
     * @param boardId 멤버 수를 조회할 보드 ID
     * @return 해당 보드의 활성 멤버 수 (MANAGER + USER)
     * board_members 테이블에서 boardId에 해당하는 멤버 수 반환
     * 메인 페이지 보드 카드의 멤버 수 표시에 사용
     */
    @GetMapping("/count/{boardId}")
    @ResponseBody
    public long getMemberCount(@PathVariable Long boardId) {
        return boardMemberService.countByBoardId(boardId);
    }
}

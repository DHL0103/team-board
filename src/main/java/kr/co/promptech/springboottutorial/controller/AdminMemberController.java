package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {
    private final MemberService memberService;

    /**
     * @param model 뷰에 전달할 데이터 컨테이너
     * @return admin/members 뷰
     * 전체 멤버 목록을 조회해 뷰에 전달
     * members(MemberResponseDto) 전달
     */
    @GetMapping
    public String adminMemberPage() {
        return "admin/members";
    }

}

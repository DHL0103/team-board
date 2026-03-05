package kr.co.promptech.springboottutorial.controller;

import kr.co.promptech.springboottutorial.model.dto.MemberCreateDto;
import kr.co.promptech.springboottutorial.model.Member;
import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    //id값으로 멤버 검색
    @GetMapping("/{id}")
    @ResponseBody
    public Member getMemberDetailPage(@PathVariable Long id){
        return memberService.getMemberById(id);
    }

    //로그인 페이지
    @GetMapping("/login")
    public String login_page() {
        return "login_form";
    }


}

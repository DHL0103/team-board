package kr.co.promptech.springboottutorial.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    //로그인 요청
    @GetMapping("/login")
    public String login(){
        return "login_form";
    }
}

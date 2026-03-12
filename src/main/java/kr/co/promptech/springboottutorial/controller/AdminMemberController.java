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

    @GetMapping
    public String adminMemberPage(Model model) {
        model.addAttribute("members", memberService.getAllMemberDto());
        return "admin/members";
    }

}

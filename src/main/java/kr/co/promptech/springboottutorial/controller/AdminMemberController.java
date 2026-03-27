package kr.co.promptech.springboottutorial.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    @GetMapping
    public String adminMemberPage() {
        return "admin/members";
    }

}

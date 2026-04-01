package kr.co.promptech.springboottutorial.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    /**
     * 관리자용 멤버 관리 페이지 렌더링
     * @return admin/members 뷰
     */
    @GetMapping
    public String adminMemberPage() {
        return "admin/members";
    }

}

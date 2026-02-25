package kr.co.promptech.springboottutorial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/request")
    public String requestPage(){


        return "admin/request";
    }

    @GetMapping("/members")
    public String memberPage(){


        return "admin/members";
    }
    @GetMapping("/boards")
    public String boardsPage(){


        return "admin/boards";
    }
}

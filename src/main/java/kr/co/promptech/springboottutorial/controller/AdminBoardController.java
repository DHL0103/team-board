package kr.co.promptech.springboottutorial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/boards")
public class AdminBoardController {

    @GetMapping
    public String adminBoardPage() {
        return "admin/boards";
    }
}

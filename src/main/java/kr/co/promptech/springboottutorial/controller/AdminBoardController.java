package kr.co.promptech.springboottutorial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/boards")
public class AdminBoardController {

    /**
     * 관리자용 보드 관리 페이지 렌더링
     * @return admin/boards 뷰
     */
    @GetMapping
    public String adminBoardPage() {
        return "admin/boards";
    }
}

package kr.co.promptech.springboottutorial.controller;

import jakarta.validation.Valid;
import kr.co.promptech.springboottutorial.model.dto.SignupRequestDto;
import kr.co.promptech.springboottutorial.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * @return 로그인 폼 뷰 이름 (login_form)
     * 로그인 페이지 렌더링, 실제 인증 처리는 Spring Security가 담당
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login_form";
    }

    /**
     * @param signupRequestDto Thymeleaf 폼 바인딩용 빈 DTO
     * @return 회원가입 폼 뷰 이름 (signup)
     */
    @GetMapping("/signup")
    public String signupPage(@ModelAttribute SignupRequestDto signupRequestDto) {
        return "signup";
    }

    /**
     * @Valid 로 필드 제약조건 검사 후 BindingResult로 오류 처리
     * 비밀번호 불일치 및 아이디 중복도 BindingResult에 추가
     * @param dto           회원가입 입력 데이터 (username, password, passwordConfirm)
     * @param bindingResult 검증 오류 결과
     * @return 검증 실패 시 signup 뷰, 성공 시 로그인 페이지로 리다이렉트
     */
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequestDto dto, BindingResult bindingResult) {
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "비밀번호가 일치하지 않습니다.");
        }
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        boolean success = memberService.signup(dto.getUsername(), dto.getPassword());
        if (!success) {
            bindingResult.rejectValue("username", "duplicate", "이미 사용 중인 아이디입니다.");
            return "signup";
        }
        return "redirect:/member/login?registered=true";
    }
}
package kr.co.promptech.springboottutorial.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "아이디를 입력해 주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 밑줄(_)만 사용 가능합니다.")
    private final String username;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
    private final String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private final String passwordConfirm;
}
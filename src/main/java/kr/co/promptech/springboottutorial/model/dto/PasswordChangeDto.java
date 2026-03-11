package kr.co.promptech.springboottutorial.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordChangeDto {

    @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
    private final String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
    private final String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해 주세요.")
    private final String newPasswordConfirm;
}
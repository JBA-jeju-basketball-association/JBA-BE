package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdatePasswordInFindPasswordRequest {
    @NotEmpty
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "비밀번호", example = "abc123123!")
    private String password;

    @NotEmpty
    @Schema(description = "비밀번호 확인", example = "abc123123!")
    private String confirmPassword;

    @Schema(description = "인증번호", example = "123456")
    private Integer certificationNum;

    @Email
    @Schema(description = "이메일", example = "abc@gmail.com")
    private String email;
}

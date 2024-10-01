package github.com.jbabe.web.dto.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
public class SignUpRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotEmpty
    @Schema(description = "이메일", example = "hansol@gmail.com")
    private String email;

    @NotEmpty
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "비밀번호", example = "abc123123!")
    private String password;

    @NotEmpty
    @Schema(description = "비밀번호 확인", example = "abc123123!")
    private String passwordConfirm;

    @NotEmpty
    @Schema(description = "이름", example = "신한솔")
    private String name;

    @NotEmpty
    @Pattern(regexp = "^01([016789])-(\\d{4})-(\\d{4})$", message = "휴대폰번호 유효성 검사 실패")
    @Schema(description = "휴대폰번호", example = "010-1234-5678")
    private String phoneNum;

    public boolean equalsPasswordAndPasswordConfirm() {
        return password.equals(passwordConfirm);
    }
}

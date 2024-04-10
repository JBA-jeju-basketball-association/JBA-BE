package github.com.jbabe.web.dto.authAccount;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;

    private String password;
}

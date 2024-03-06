package github.com.jbabe.web.dto.authAccount;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    @Email(message = "이메일 형식을 확인해주세요.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;
}

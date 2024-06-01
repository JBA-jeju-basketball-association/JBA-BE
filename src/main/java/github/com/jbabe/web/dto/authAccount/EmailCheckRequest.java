package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailCheckRequest {
    @Email(message = "이메일 형식을 확인해주세요.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Schema(description = "이메일", example = "hansol@gmail.com")
    private String email;

    @NotEmpty(message = "인증 번호를 입력해 주세요.")
    @Schema(description = "인증번호(6자리의 랜덤 숫자)", example = "123456")
    private String authNum;
}

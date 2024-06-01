package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    @Email(message = "이메일 형식을 확인해주세요.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Schema(description = "이메일", example = "hansol@gmail.com")
    private String email;
}

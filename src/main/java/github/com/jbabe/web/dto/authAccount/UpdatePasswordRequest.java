package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdatePasswordRequest {
    @NotBlank(message = "비어있을 수 없습니다.")
    private String prevPW;

    @NotBlank(message = "비어있을 수 없습니다.")
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "새 비밀번호", example = "abc123123!")
    private String newPW;

    @NotBlank(message = "비어있을 수 없습니다.")
    @Schema(description = "새 비밀번호 확인", example = "abc123123!")
    private String newPWConfirm;
}

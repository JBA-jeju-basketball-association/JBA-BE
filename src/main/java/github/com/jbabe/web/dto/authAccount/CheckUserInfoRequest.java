package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class CheckUserInfoRequest {
    @NotEmpty
    @Schema(description = "이름", example = "신한솔")
    private String name;

    @Min(value = 10000000, message = "생년월일은 8자리입니다.")
    @Max(value = 99999999, message = "생년월일은 8자리입니다.")
    private Integer birth;

    @Email
    @NotEmpty
    private String email;
}

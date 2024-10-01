package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class CheckUserInfoRequest {
    @NotEmpty
    @Schema(description = "이름", example = "신한솔")
    private String name;

    @Email
    @NotEmpty
    private String email;
}

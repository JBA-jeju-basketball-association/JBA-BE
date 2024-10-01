package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateAccountRequest {
    @NotEmpty
    @Schema(description = "이름", example = "신한솔")
    private String name;

    @NotEmpty
    @Pattern(regexp = "^01([016789])-(\\d{4})-(\\d{4})$", message = "휴대폰번호 유효성 검사 실패")
    @Schema(description = "휴대폰번호", example = "010-1234-5678")
    private String phoneNum;

}

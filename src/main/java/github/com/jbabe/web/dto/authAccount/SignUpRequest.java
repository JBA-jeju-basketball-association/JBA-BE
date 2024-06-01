package github.com.jbabe.web.dto.authAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

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
    @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})+$", message = "휴대폰번호 유효성 검사 실패")
    @Schema(description = "휴대폰번호", example = "010-1234-5678")
    private String phoneNum;

    @NotEmpty
    @Schema(description = "성별", example = "MALE")
    private String gender;

    @Min(1900)
    @Max(2024)
    private Integer year;

    @Min(1)
    @Max(12)
    private Integer month;

    @Min(1)
    @Max(31)
    private Integer day;

    @NotEmpty
    @Schema(description = "소속팀", example = "제농회")
    private String team;

    public boolean equalsPasswordAndPasswordConfirm() {
        return password.equals(passwordConfirm);
    }
}

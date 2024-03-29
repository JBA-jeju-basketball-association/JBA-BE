package github.com.jbabe.web.dto.authAccount;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
public class SignUpRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotEmpty
    private String email;
    @NotEmpty
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하여야 합니다.")
    private String password;

    @NotEmpty
    private String passwordConfirm;

    @NotEmpty
    private String name;
    @NotEmpty
    @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})+$", message = "휴대폰번호 유효성 검사 실패")
    private String phoneNum;

    @NotEmpty
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
    private String team;

    public boolean equalsPasswordAndPasswordConfirm() {
        return password.equals(passwordConfirm);
    }
}

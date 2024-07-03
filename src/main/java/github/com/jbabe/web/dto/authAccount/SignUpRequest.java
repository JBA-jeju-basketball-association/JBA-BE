package github.com.jbabe.web.dto.authAccount;

import github.com.jbabe.repository.user.User;
import github.com.jbabe.service.exception.ConflictException;
import github.com.jbabe.service.exception.InvalidReqeustException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    @Schema(description = "주민번호 앞 7자리", example = "9602161")
    private String birth;

    @NotEmpty
    @Schema(description = "소속팀", example = "제농회")
    private String team;

    public boolean equalsPasswordAndPasswordConfirm() {
        return password.equals(passwordConfirm);
    }

    public LocalDate getBirthByLocalDate() {

        String dateStr = birth.substring(0, 6);
        LocalDate date;

        // 추출한 6자리 문자열을 yyMMdd 형식의 LocalDate로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        try {
             date = LocalDate.parse(dateStr, formatter);
        }catch (Exception e) {
            throw new InvalidReqeustException("주민번호 유효성 검사 실패", birth);
        }
        int currentYear = LocalDate.now().getYear();
        if (date.getYear() > currentYear) {
            date = date.withYear(date.getYear() - 100);
        }

        return date;
    }

    public User.Gender transformGender() {
        String num = birth.substring(7, 8);
        if (num.equals("1") || num.equals("3")) {
            return User.Gender.MALE;
        }else if (num.equals("2") || num.equals("4")){
            return User.Gender.FEMALE;
        }else {
            throw new InvalidReqeustException("주민번호 유효성 검사 실패", birth);
        }
    }
}

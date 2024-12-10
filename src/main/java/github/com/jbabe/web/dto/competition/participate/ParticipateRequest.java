package github.com.jbabe.web.dto.competition.participate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class ParticipateRequest {
    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "이름", example = "즐라탄")
    private String name;
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01([016789])-(\\d{4})-(\\d{4})$", message = "휴대폰번호 유효성 검사 실패")
    private String phoneNum;
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    @Schema(description = "이메일", example = "abc@abc.com")
    private String email;
    @JsonIgnore
    @Setter
    private List<FileDto> files;

}

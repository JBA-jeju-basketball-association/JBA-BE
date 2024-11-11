package github.com.jbabe.web.dto.competition.participate;

import github.com.jbabe.web.dto.storage.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Schema(name = "files",description = "첨부파일들", example = "[\n" +
            "    {\n" +
            "        \"fileName\": \"참여신청서.hwp\",\n" +
            "        \"fileUrl\": \"url~~~\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"2222.png\",\n" +
            "        \"fileUrl\": \"2222.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"3333.png\",\n" +
            "        \"fileUrl\": \"3333.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"4444.png\",\n" +
            "        \"fileUrl\": \"4444.url\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"fileName\": \"5555.png\",\n" +
            "        \"fileUrl\": \"5555.url\"\n" +
            "    }\n" +
            "]"
    )
    @NotNull(message = "적어도 한개의 imgs는 필수입니다.")
    private List<FileDto> files;
}

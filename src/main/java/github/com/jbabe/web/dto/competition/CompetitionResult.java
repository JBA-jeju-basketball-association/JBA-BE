package github.com.jbabe.web.dto.competition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Valid
public class CompetitionResult {
    private Integer competitionRecordId;

    @NotNull(message = "종별을 입력해주세요.")
    @NotBlank(message = "종별을 입력해주세요.")
    private String division;

    @NotNull(message = "HOME 팀명을 입력해주세요.")
    @NotBlank(message = "HOME 팀명을 입력해주세요.")
    private String homeName;

    @Min(value = 0, message = "점수는 0점 이상입니다.")
    @Max(value = 200, message = "점수는 200점 이하입니다.")
    private Integer homeScore;

    @NotBlank(message = "AWAY 팀명을 입력해주세요.")
    @NotNull(message = "AWAY 팀명을 입력해주세요.")
    private String awayName;

    @Min(value = 0, message = "점수는 0점 이상입니다.")
    @Max(value = 200, message = "점수는 200점 이하입니다.")
    private Integer awayScore;

    @NotNull(message = "시작일을 입력해주세요.")
    private LocalDateTime startTime;

    private String fileName;
    private String fileUrl;
}

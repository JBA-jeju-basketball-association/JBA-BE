package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Valid
public class PostResultRequestRow {

    @Schema(description = "결과아이디", example = "123")
    private Integer competitionResultId;

    @NotNull(message = "경기 번호가 없습니다.")
    @Schema(description = "경기번호", example = "2")
    private Integer gameNumber;

    @NotNull(message = "시작일을 입력해주세요.")
    private LocalDateTime startDate;

    @NotNull(message = "스테이지를 입력해주세요.")
    @NotBlank(message = "스테이지를 입력해주세요.")
    @Schema(description = "floor", example = "결승")
    private String floor;

    @NotNull(message = "장소를 입력해주세요.")
    @Schema(description = "장소", example = "제주제일중학교")
    private String place;

    @NotNull(message = "HOME 팀명을 입력해주세요.")
    @NotBlank(message = "HOME 팀명을 입력해주세요.")
    @Schema(description = "홈팀명", example = "제농회")
    private String homeName;

    @Min(value = 0, message = "점수는 0점 이상입니다.")
    @Max(value = 200, message = "점수는 200점 이하입니다.")
    @NotNull(message = "점수를 입력해주세요.")
    @Schema(description = "홈팀 점수", example = "80")
    private Integer homeScore;

    @NotBlank(message = "AWAY 팀명을 입력해주세요.")
    @NotNull(message = "AWAY 팀명을 입력해주세요.")
    @Schema(description = "어웨이팀명", example = "리딤")
    private String awayName;

    @Min(value = 0, message = "점수는 0점 이상입니다.")
    @Max(value = 200, message = "점수는 200점 이하입니다.")
    @NotNull(message = "점수를 입력해주세요.")
    @Schema(description = "어웨이팀 점수", example = "70")
    private Integer awayScore;

    @Schema(description = "파일이름", example = "결승전(제농회vs리딤) 기록지")
    private String fileName;

    @Schema(description = "기록지URL", example = "~~~~~~~~~")
    private String filePath;

    @NotNull(message = "5대5 경기여부를 입력해주세요.")
    @Schema(description = "5대5 경기 여부", example = "true")
    private boolean state5x5;
}

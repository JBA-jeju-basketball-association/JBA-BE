package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Valid
@NoArgsConstructor
@AllArgsConstructor
public class PostCompetitionScheduleRow {
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

    @NotBlank(message = "AWAY 팀명을 입력해주세요.")
    @NotNull(message = "AWAY 팀명을 입력해주세요.")
    @Schema(description = "어웨이팀명", example = "리딤")
    private String awayName;

    @NotNull(message = "5대5 경기여부를 입력해주세요.")
    @Schema(description = "5대5 경기 여부", example = "true")
    private boolean state5x5;
}

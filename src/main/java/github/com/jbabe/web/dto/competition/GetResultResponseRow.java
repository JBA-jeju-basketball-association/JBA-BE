package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetResultResponseRow {
    @Schema(description = "결과아이디", example = "123")
    private Integer competitionResultId;

    @Schema(description = "경기번호", example = "2")
    private Integer gameNumber;

    private LocalDateTime startDate;

    @Schema(description = "floor", example = "결승")
    private String floor;

    @Schema(description = "장소", example = "제주제일중학교")
    private String place;

    @Schema(description = "홈팀명", example = "제농회")
    private String homeName;

    @Schema(description = "홈팀 점수", example = "80")
    private Integer homeScore;

    @Schema(description = "어웨이팀명", example = "리딤")
    private String awayName;

    @Schema(description = "어웨이팀 점수", example = "70")
    private Integer awayScore;

    @Schema(description = "파일이름", example = "결승전(제농회vs리딤) 기록지")
    private String fileName;

    @Schema(description = "기록지URL", example = "~~~~~~~~~")
    private String filePath;

    @Schema(description = "5대5 경기 여부", example = "true")
    private boolean state5x5;
}

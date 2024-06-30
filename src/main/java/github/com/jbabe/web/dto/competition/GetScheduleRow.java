package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetScheduleRow {
    public Integer competitionResultId;
    private Integer gameNumber;
    private LocalDateTime startDate;
    private String floor;
    private String place;
    private String homeName;
    private String awayName;
    private boolean state5x5;
}

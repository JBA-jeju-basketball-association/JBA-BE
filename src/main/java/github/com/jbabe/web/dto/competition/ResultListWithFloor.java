package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultList {
    private String floor;
    private String division;
    private LocalDateTime time;
    private String homeName;
    private Integer homeScore;
    private String awayName;
    private Integer awayScore;
    private String filePath;
    private String fileName;

    public ResultList(String division, LocalDateTime time, String homeName, Integer homeScore, String awayName, Integer awayScore, String filePath, String fileName) {
        this.division = division;
        this.time = time;
        this.homeName = homeName;
        this.homeScore = homeScore;
        this.awayName = awayName;
        this.awayScore = awayScore;
        this.filePath = filePath;
        this.fileName = fileName;
    }
}

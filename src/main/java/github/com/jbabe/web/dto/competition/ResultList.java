package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultList {
    private String division;
    private LocalDateTime time;
    private String homeName;
    private Integer homeScore;
    private String awayName;
    private Integer awayScore;
    private String filePath;
    private String fileName;
}

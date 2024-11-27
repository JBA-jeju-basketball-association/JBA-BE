package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainCompetitionResponse {
    private Integer competitionId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> places;
}

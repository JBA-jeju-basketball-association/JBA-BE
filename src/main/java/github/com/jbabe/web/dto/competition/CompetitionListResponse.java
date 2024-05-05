package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionListResponse {
    private Integer competitionId;
    private String title;
    private String division;
    private Date startDate;
    private Date endDate;
}

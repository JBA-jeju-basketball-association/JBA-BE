package github.com.jbabe.web.dto.competition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetResultWithTitleResponse {
    private String title;
    private Date startDate;
    private Date endDate;
    private List<String> divisionList;
    private List<AddCompetitionResultRequest> resultResponse;
}

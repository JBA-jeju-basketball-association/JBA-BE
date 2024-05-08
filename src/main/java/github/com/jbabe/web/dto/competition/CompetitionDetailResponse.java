package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionDetailResponse {
    private Integer competitionId;
    private String title;
    private Date startDate;
    private Date endDate;
    private String relatedUrl;
    private String content;
    private List<CompetitionDetailPlace> places;
    private List<CompetitionDetailAttachedFile> competitionDetailAttachedFiles;
    private List<String> divisions;
    private Boolean existResult;
}

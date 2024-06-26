package github.com.jbabe.web.dto.competition;

import github.com.jbabe.repository.competition.Competition;
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
    private Competition.Phase phase;
    private List<CompetitionDetailPlace> places;
    private List<CompetitionDetailAttachedFile> competitionDetailAttachedFiles;
    private List<String> divisions;
    private List<String> ckImgUrls;
}

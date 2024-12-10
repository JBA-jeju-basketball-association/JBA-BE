package github.com.jbabe.web.dto.competition;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.web.dto.division.DivisionDto;
import lombok.*;

import java.time.LocalDate;
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
    private LocalDate startDate;
    private LocalDate endDate;
    private String relatedUrl;
    private String content;
    private Competition.Phase phase;
    private LocalDate participationStartDate;
    private LocalDate participationEndDate;
    private List<CompetitionDetailPlace> places;
    private List<CompetitionDetailAttachedFile> competitionDetailAttachedFiles;
    private List<DivisionDto> divisions;
    private List<String> ckImgUrls;
}

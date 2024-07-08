package github.com.jbabe.web.dto.competition;

import github.com.jbabe.repository.competition.Competition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCompetitionAdminListResponse {
    private Integer competitionId;
    private String userEmail;
    private String situation;
    private Competition.Phase phase;
    private List<String> divisions;
    private String competitionName;
    private Date startDate;
    private Date endDate;
    private String content;
    private String link;
    private List<CompetitionDetailAttachedFile> files;
    private Competition.CompetitionStatus status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;

    public GetCompetitionAdminListResponse(Integer competitionId, String userEmail, String situation, Competition.Phase phase, String competitionName, Date startDate, Date endDate, String content, String link, Competition.CompetitionStatus status, LocalDateTime createAt, LocalDateTime updateAt, LocalDateTime deleteAt) {
        this.competitionId = competitionId;
        this.userEmail = userEmail;
        this.situation = situation;
        this.phase = phase;
        this.divisions = new ArrayList<>();
        this.competitionName = competitionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.content = content;
        this.link = link;
        this.files = new ArrayList<>();
        this.status = status;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.deleteAt = deleteAt;
    }
}

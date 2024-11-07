package github.com.jbabe.web.dto.competition.participate;

import github.com.jbabe.web.dto.storage.FileDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SimplyParticipateResponse {
    private Long participationCompetitionId;
    private String competitionName;
    private String divisionName;
    private LocalDateTime applicantDate;
    private LocalDateTime updatedAt;
    private LocalDate participationStartDate;
    private LocalDate participationEndDate;

}

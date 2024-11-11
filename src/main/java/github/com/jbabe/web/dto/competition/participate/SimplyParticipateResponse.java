package github.com.jbabe.web.dto.competition.participate;

import github.com.jbabe.web.dto.infinitescrolling.ScrollingResponseInterface;
import github.com.jbabe.web.dto.infinitescrolling.criteria.CursorHolder;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchCriteria;
import github.com.jbabe.web.dto.infinitescrolling.criteria.SearchRequest;
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
public class SimplyParticipateResponse  implements ScrollingResponseInterface<SearchCriteria> {
    private Long participationCompetitionId;
    private String competitionName;
    private String divisionName;
    private LocalDateTime applicantDate;
    private LocalDateTime updatedAt;
    private LocalDate participationStartDate;
    private LocalDate participationEndDate;

    @Override
    public String nextCursor(SearchCriteria criteria) {
        return this.applicantDate.toString();
    }

    @Override
    public long nextIdCursor() {
        return this.participationCompetitionId;
    }

    public boolean valueValidity(SearchRequest request){
        CursorHolder holder = request.getCursorHolder();
        return this.participationCompetitionId.equals(holder.getIdCursor()) && this.applicantDate.equals(holder.getCreatedAtCursor());
    }
}

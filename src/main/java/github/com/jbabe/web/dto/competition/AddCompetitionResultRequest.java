package github.com.jbabe.web.dto.competition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddCompetitionResultRequest {
    @NotNull(message = "스테이지를 입력해주세요.")
    private String floor;

    @Valid
    private List<CompetitionResult> competitionResult;
}

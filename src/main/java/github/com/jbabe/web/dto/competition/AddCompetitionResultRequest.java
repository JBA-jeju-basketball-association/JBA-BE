package github.com.jbabe.web.dto.competition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCompetitionResultRequest {
    @NotNull(message = "스테이지를 입력해주세요.")
    private String floor;

    @Valid
    @NotNull(message = "스테이지를 입력해주세요.")
    private List<CompetitionResult> competitionResult;
}

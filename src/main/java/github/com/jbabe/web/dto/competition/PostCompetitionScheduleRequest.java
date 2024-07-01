package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Valid
public class PostCompetitionScheduleRequest {

    @NotNull(message = "종별을 입력해주세요.")
    @NotBlank(message = "종별을 입력해주세요.")
    @Schema(description = "종별", example = "element")
    private String division;

    @Valid
    @NotNull(message = "일정을 입력해주세요")
    private List<PostCompetitionScheduleRow> postCompetitionScheduleRow;
}

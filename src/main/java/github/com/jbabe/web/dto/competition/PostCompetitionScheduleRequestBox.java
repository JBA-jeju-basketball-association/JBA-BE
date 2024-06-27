package github.com.jbabe.web.dto.competition;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PostCompetitionScheduleRequestBox {
    @Valid
    private List<PostCompetitionScheduleRequest> request;
}

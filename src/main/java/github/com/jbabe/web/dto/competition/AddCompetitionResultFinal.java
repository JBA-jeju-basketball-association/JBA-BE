package github.com.jbabe.web.dto.competition;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCompetitionResultFinal {
    @Valid
    private List<AddCompetitionResultRequest> requests;
}

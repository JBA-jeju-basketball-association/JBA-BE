package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetResultResponse {
    private String division;
    private List<GetResultResponseRow> getResultResponseRows;
}

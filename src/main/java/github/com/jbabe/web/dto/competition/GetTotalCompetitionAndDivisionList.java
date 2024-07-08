package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTotalCompetitionAndDivisionList {
    private Integer totalSize;
    private List<String> divisionList;
}

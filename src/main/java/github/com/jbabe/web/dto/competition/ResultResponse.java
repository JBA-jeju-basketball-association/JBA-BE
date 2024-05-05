package github.com.jbabe.web.dto.competition;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultResponse {
    private String floor;
    private List<ResultList> resultList;
}

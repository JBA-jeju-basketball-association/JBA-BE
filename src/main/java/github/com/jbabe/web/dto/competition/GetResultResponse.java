package github.com.jbabe.web.dto.competition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class getResultResponse {
    private List<String> divisionList;
    private List<ResultResponse> resultResponse;
}

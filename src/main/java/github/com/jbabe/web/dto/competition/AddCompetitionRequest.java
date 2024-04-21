package github.com.jbabe.web.dto.competition;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AddCompetitionRequest {
    @NotNull(message = "제목을 입력해주세요.")
    private String title;

    @NotEmpty(message = "종별을 선택해주세요.")
    private List<String> divisions;

    @NotNull(message = "시작일을 입력해주세요.")
    private Date startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    private Date endDate;

    @NotEmpty(message = "장소를 등록해주세요.")
    private List<place> places;

    private String relatedURL;

    private String skData;

    private List<String> realCkImgs;
}

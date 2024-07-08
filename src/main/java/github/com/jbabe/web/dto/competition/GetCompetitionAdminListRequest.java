package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.util.Date;

@Getter
public class GetCompetitionAdminListRequest {

    @Schema(description = "검색 타입", example = "대회명")
    private String searchType;

    @Schema(description = "검색어", example = "도민체전")
    private String searchKey;

    @Schema(description = "날짜필터 시작일", example = "2024-05-16")
    private Date filterStartDate;

    @Schema(description = "날짜필터 종료일", example = "2024-05-17")
    private Date filterEndDate;

    @Schema(description = "종별", example = "초등")
    private String division;

    @Schema(description = "현황", example = "예정")
    private String situation;



}

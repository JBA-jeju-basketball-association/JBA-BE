package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Getter
@Setter
public class UpdateCompetitionRequest {
    @NotNull(message = "제목을 입력해주세요.")
    @NotBlank(message = "제목을 입력해주세요.")
    @Schema(description = "대회 제목", example = "2024년도 제주특별자치도 도민체전")
    private String title;

    @NotEmpty(message = "종별을 선택해주세요.")
    @Schema(description = "종별", example = "element")
    private List<String> divisions;

    @NotNull(message = "시작일을 입력해주세요.")
    private Date startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    private Date endDate;

    @NotEmpty(message = "장소를 등록해주세요.")
    private List<UpdatePlace> updatePlaces;

    @Schema(description = "관련URL", example = "https://www.koreabasketball.or.kr/main/")
    private String relatedURL;

    @Schema(description = "ckDate", example = "<p>POST 페이지 더미데이터입니다.</p><p>&nbsp;</p><p>&nbsp;</p><p>일정 : 6월 6일 (목) 현충일</p><p>&nbsp;</p><p>장소 : 이디야커피랩(언주역 근처)</p><p>&nbsp;</p><p>내용 :&nbsp;</p><p>[JBA Project Meeting]</p><p>(1) 기능 개발 마감일로 이전 PR 완료 필요</p><p>(2) 완료된 기능 리뷰 및 진행 상황 공유</p><p>(3) 각 페이지별 디자인 확정 논의</p><p>(4) 다음 목표 및 일정 협의</p><p>&nbsp;</p><figure class=\"image\"><img style=\"aspect-ratio:500/281;\" src=\"https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/a649a96d-cfb0-45e2-afd7-f98529353c3f.jpg\" width=\"500\" height=\"281\"></figure>")
    private String ckData;

    @Schema(description = "ckImg", example = "https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/575e3d52-fe9c-4248-8913-0b6e7b1bec25.png")
    private List<String> realCkImgs;

    @Schema(description = "기존 업데이트 되었던 파일 URL", example = "https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/575e3d52-fe9c-4248-8913-0b6e7b1bec25.png")
    private List<String> uploadedAttachedFiles;

    @Schema(description = "기존 업데이트 되었던 이미지 URL", example = "https://sirimp-bucket.s3.ap-northeast-2.amazonaws.com/575e3d52-fe9c-4248-8913-0b6e7b1bec25.png")
    private List<String> deletedCkImgUrls;
}

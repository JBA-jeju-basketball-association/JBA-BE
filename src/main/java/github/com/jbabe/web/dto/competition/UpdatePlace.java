package github.com.jbabe.web.dto.competition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlace {
    @Schema(description = "competitionPlaceId", example = "1")
    private Integer competitionPlaceId;

    @Schema(description = "장소명", example = "구좌체육관")
    private String placeName;

    @Schema(description = "주소", example = "제주특별자치도 제주시 구좌읍 김녕리 497-1")
    private String address;

    @Schema(description = "위도", example = "33.5562753899747")
    private Double latitude;

    @Schema(description = "경도", example = "126.762179075482")
    private Double longitude;
}



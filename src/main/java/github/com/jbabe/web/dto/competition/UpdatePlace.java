package github.com.jbabe.web.dto.competition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlace {
    private Integer competitionPlaceId;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
}



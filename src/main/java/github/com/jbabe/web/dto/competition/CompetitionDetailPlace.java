package github.com.jbabe.web.dto.competition;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionDetailPlace {
    private Integer competitionPlaceId;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String address;
}

package github.com.jbabe.web.dto.competition;

import lombok.Getter;

@Getter
public class Place {
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
}

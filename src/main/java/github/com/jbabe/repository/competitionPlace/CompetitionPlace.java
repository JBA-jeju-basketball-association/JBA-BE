package github.com.jbabe.repository.competitionPlace;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "competition_place")
@Entity
@Builder
public class CompetitionPlace{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_place_id")
    private Integer competitionPlaceId;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "address", nullable = false)
    private String address;
}

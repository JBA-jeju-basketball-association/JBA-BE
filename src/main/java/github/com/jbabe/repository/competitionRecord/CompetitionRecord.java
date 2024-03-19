package github.com.jbabe.repository.competitionRecord;

import github.com.jbabe.repository.division.Division;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "competition_record")
public class CompetitionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_record_id")
    private Integer competitionRecordId;

    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "home_name", nullable = false)
    private String homeName;

    @Column(name = "home_score", nullable = false)
    private Integer homeScore;

    @Column(name = "away_name", nullable = false)
    private String awayName;

    @Column(name = "away_score", nullable = false)
    private Integer awayScore;
}

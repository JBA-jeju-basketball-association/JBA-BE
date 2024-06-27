package github.com.jbabe.repository.competitionRecord;

import github.com.jbabe.repository.division.Division;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "competition_record")
public class CompetitionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_record_id")
    private Integer competitionRecordId;

    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    @Column(name = "floor", nullable = false)
    private String floor;

    @Column(name = "place", nullable = false)
    private String place;

    @Column(name = "game_number", nullable = false)
    private Integer gameNumber;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "home_name", nullable = false)
    private String homeName;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_name", nullable = false)
    private String awayName;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "file_path", unique = true)
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_5x5")
    private boolean state5x5;

}

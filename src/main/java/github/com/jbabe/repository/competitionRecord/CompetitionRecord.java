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

    @Column(name = "crew_chief_id")
    private Integer crewChiefId;

    @Column(name = "crew_chief_name")
    private String crewChiefName;

    @Column(name = "crew_chief_email")
    private String crewChiefEmail;

    @Column(name = "umpire1_id")
    private Integer umpire1Id;

    @Column(name = "umpire1_name")
    private String umpire1Name;

    @Column(name = "umpire1_email")
    private String umpire1Email;

    @Column(name = "umpire2_id")
    private Integer umpire2Id;

    @Column(name = "umpire2_name")
    private String umpire2Name;

    @Column(name = "umpire2_email")
    private String umpire2Email;

    @Column(name = "timer_id")
    private Integer timerId;

    @Column(name = "timer_name")
    private String timerName;

    @Column(name = "timer_email")
    private String timerEmail;

    @Column(name = "shot_clock_operator_id")
    private Integer shotClockOperatorId;

    @Column(name = "shot_clock_operator_name")
    private String shotClockOperatorName;

    @Column(name = "shot_clock_operator_email")
    private String shotClockOperatorEmail;

    @Column(name = "scorer_id")
    private Integer scorerId;

    @Column(name = "scorer_name")
    private String scorerName;

    @Column(name = "scorer_email")
    private String scorerEmail;

    @Column(name = "assistant_scorer_id")
    private Integer assistantScorerId;

    @Column(name = "assistant_scorer_name")
    private String assistantScorerName;

    @Column(name = "assistant_scorer_email")
    private String assistantScorerEmail;

    @Column(name = "commissioner_id")
    private Integer commissionerId;

    @Column(name = "commissioner_name")
    private String commissionerName;

    @Column(name = "commissioner_email")
    private String commissionerEmail;

    @Column(name = "file_path", unique = true)
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

}

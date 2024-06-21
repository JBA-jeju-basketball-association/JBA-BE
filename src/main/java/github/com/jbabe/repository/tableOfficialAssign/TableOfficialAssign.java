package github.com.jbabe.repository.tableOfficialAssign;

import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "table_official_assign")
public class TableOfficialAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_official_assign_id")
    private Integer tableOfficialAssignId;

    @OneToOne
    @JoinColumn(name = "competition_record_id", nullable = false)
    private CompetitionRecord competitionRecord;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "timer_id")
    private Integer timerId;

    @Column(name = "timer_name")
    private String timerName;

    @Column(name = "shot_clock_operator_id")
    private Integer shotClockOperatorId;

    @Column(name = "shot_clock_operator_name")
    private String shotClockOperatorName;

    @Column(name = "scorer_id")
    private Integer scorerId;

    @Column(name = "scorer_name")
    private String scorerName;

    @Column(name = "assistant_scorer_id")
    private Integer assistantScorerId;

    @Column(name = "assistant_scorer_name")
    private String assistantScorerName;

    @Column(name = "commissioner_id")
    private Integer commissionerId;

    @Column(name = "commissioner_name")
    private String commissionerName;

    @Column(name = "memo")
    private String memo;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;
}

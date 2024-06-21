package github.com.jbabe.repository.refereeAssign;

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
@Table(name = "referee_assign")
public class RefereeAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referee_assign_id")
    private Integer RefereeAssignId;

    @OneToOne
    @JoinColumn(name = "competition_record_id", nullable = false)
    private CompetitionRecord competitionRecord;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "crew_chief_id")
    private Integer crewChiefId;

    @Column(name = "crew_chief_name")
    private String crewChiefName;

    @Column(name = "umpire1_id")
    private Integer umpire1Id;

    @Column(name = "umpire1_name")
    private String umpire1Name;

    @Column(name = "umpire2_id")
    private Integer umpire2Id;

    @Column(name = "umpire2_name")
    private String umpire2Name;

    @Column(name = "memo")
    private String memo;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;
}

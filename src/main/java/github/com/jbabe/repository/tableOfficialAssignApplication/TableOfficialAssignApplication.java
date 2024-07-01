package github.com.jbabe.repository.tableOfficialAssignApplication;

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
@Table(name = "table_official_assign_application")
public class TableOfficialAssignApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_official_assign_application_id")
    private Integer tableOfficialAssignApplicationId;

    // 연결된 competition_record 데이터가 삭제될 경우 competitionRecord=null
    @ManyToOne
    @JoinColumn(name = "competition_record_id")
    private CompetitionRecord competitionRecord;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "memo")
    private String memo;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;
}
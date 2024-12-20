package github.com.jbabe.repository.division;

import github.com.jbabe.repository.competition.Competition;
import github.com.jbabe.repository.competitionRecord.CompetitionRecord;
import github.com.jbabe.repository.competitionuser.ParticipationCompetition;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "division")
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "division_id")
    private Integer divisionId;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(name = "division_name", nullable = false)
    private String divisionName;

    @OneToMany(mappedBy = "division", fetch = FetchType.LAZY)
    private List<CompetitionRecord> competitionRecords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_enum_id")
    private DivisionEnum divisionEnum;

    @OneToMany(mappedBy = "division", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipationCompetition> participationCompetitions;

    public Division(Integer divisionId) {
        this.divisionId = divisionId;
    }

}

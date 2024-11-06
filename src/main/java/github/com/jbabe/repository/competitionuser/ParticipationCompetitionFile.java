package github.com.jbabe.repository.competitionuser;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

@Entity
@Setter
@NoArgsConstructor
@Getter
@Table(name = "participation_competition_attached_file")
public class ParticipationCompetitionFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_competition_attached_file_id")
    private Long participationCompetitionFileId;
    @ManyToOne
    @JoinColumn(name = "participation_competition_id")
    private ParticipationCompetition participationCompetition;

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_path")
    private String filePath;
}
